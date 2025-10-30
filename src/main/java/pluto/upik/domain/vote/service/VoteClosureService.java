package pluto.upik.domain.vote.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.shared.ai.service.AIService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 투표 종료 처리를 담당하는 서비스
 * 스케줄러와 실시간 종료 모두에서 사용됨
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoteClosureService {

    private final VoteRepository voteRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final AIService aiService;

    /**
     * 모든 종료 조건을 확인하고 투표를 종료합니다.
     *
     * @return 종료된 투표 수
     */
    @Transactional
    public int checkAndCloseAllVotes() {
        LocalDate today = LocalDate.now();
        log.info("투표 종료 조건 확인 시작: {}", today);

        // 날짜 기준 종료 조건 확인
        List<Vote> votesToClose = voteRepository.findByStatusAndFinishedAtBefore(Vote.Status.OPEN, today);
        log.info("날짜 기준 종료 대상 투표 수: {}", votesToClose.size());

        // 참여자 수 기준 종료 조건 확인
        List<Vote> openVotes = voteRepository.findByStatus(Vote.Status.OPEN);
        for (Vote vote : openVotes) {
            if (vote.getParticipantThreshold() != null) {
                Long participantCount = voteResponseRepository.countByVoteId(vote.getId());
                if (participantCount >= vote.getParticipantThreshold()) {
                    votesToClose.add(vote);
                    log.info("참여자 수 기준 종료 대상 투표: {}, 참여자 수: {}, 기준: {}",
                            vote.getId(), participantCount, vote.getParticipantThreshold());
                }
            }
        }

        // 중복 제거
        List<Vote> uniqueVotesToClose = votesToClose.stream().distinct().toList();

        // 투표 종료 처리
        for (Vote vote : uniqueVotesToClose) {
            closeVote(vote);
        }

        log.info("투표 종료 처리 완료. 총 {}개 투표 종료됨", uniqueVotesToClose.size());
        return uniqueVotesToClose.size();
    }

    /**
     * 특정 투표의 참여자 수 기준 종료 조건을 실시간으로 확인합니다.
     *
     * @param voteId 확인할 투표 ID
     * @return 투표가 종료되었으면 true, 아니면 false
     */
    @Transactional
    public boolean checkAndCloseVoteByParticipantCount(UUID voteId) {
        Vote vote = voteRepository.findById(voteId).orElse(null);

        if (vote == null) {
            log.warn("투표를 찾을 수 없습니다: {}", voteId);
            return false;
        }

        // 이미 종료된 투표
        if (vote.getStatus() == Vote.Status.CLOSED) {
            return false;
        }

        // 참여자 수 기준이 설정되지 않은 투표
        if (vote.getParticipantThreshold() == null) {
            return false;
        }

        // 현재 참여자 수 확인
        Long participantCount = voteResponseRepository.countByVoteId(voteId);

        // 참여자 수가 기준치에 도달했는지 확인
        if (participantCount >= vote.getParticipantThreshold()) {
            log.info("참여자 수 기준 도달로 투표 종료: voteId={}, 참여자수={}, 기준={}",
                    voteId, participantCount, vote.getParticipantThreshold());
            closeVote(vote);
            return true;
        }

        return false;
    }

    /**
     * 투표를 종료하고 AI 가이드를 생성합니다.
     *
     * @param vote 종료할 투표
     */
    @Transactional
    public void closeVote(Vote vote) {
        vote.setStatus(Vote.Status.CLOSED);
        voteRepository.save(vote);

        log.info("투표 종료 처리 완료: {}", vote.getId());

        // AI를 통해 가이드 자동 생성
        try {
            log.info("투표 종료 후 AI 가이드 생성 시작: voteId={}", vote.getId());
            aiService.generateAndSaveGuide(vote.getId(), vote.getCategory());
            log.info("투표 종료 후 AI 가이드 생성 완료: voteId={}", vote.getId());
        } catch (Exception e) {
            log.error("투표 종료 후 AI 가이드 생성 실패: voteId={}, error={}", vote.getId(), e.getMessage(), e);
            // 가이드 생성 실패해도 투표 종료는 정상 처리됨
        }
    }

    /**
     * 특정 투표의 현재 종료 상태를 확인합니다.
     *
     * @param voteId 투표 ID
     * @return 종료 상태 정보
     */
    @Transactional(readOnly = true)
    public VoteClosureStatus getVoteClosureStatus(UUID voteId) {
        Vote vote = voteRepository.findById(voteId).orElse(null);

        if (vote == null) {
            return new VoteClosureStatus(false, "투표를 찾을 수 없습니다", null, null, null);
        }

        boolean isClosed = vote.getStatus() == Vote.Status.CLOSED;
        LocalDate today = LocalDate.now();
        boolean isDatePassed = vote.isFinishedByDate(today);

        Long participantCount = null;
        Boolean isThresholdReached = null;

        if (vote.getParticipantThreshold() != null) {
            participantCount = voteResponseRepository.countByVoteId(voteId);
            isThresholdReached = participantCount >= vote.getParticipantThreshold();
        }

        String reason = isClosed ? "이미 종료됨" :
                       isDatePassed ? "날짜 기준 종료" :
                       (isThresholdReached != null && isThresholdReached) ? "참여자 수 기준 종료" :
                       "진행 중";

        return new VoteClosureStatus(isClosed, reason, isDatePassed, participantCount, isThresholdReached);
    }

    /**
     * 투표 종료 상태 정보
     */
    public static class VoteClosureStatus {
        public final boolean isClosed;
        public final String reason;
        public final Boolean isDatePassed;
        public final Long participantCount;
        public final Boolean isThresholdReached;

        public VoteClosureStatus(boolean isClosed, String reason, Boolean isDatePassed,
                                Long participantCount, Boolean isThresholdReached) {
            this.isClosed = isClosed;
            this.reason = reason;
            this.isDatePassed = isDatePassed;
            this.participantCount = participantCount;
            this.isThresholdReached = isThresholdReached;
        }
    }
}
