package pluto.upik.shared.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.notification.data.model.Notification;
import pluto.upik.domain.notification.repository.NotificationRepository;
import pluto.upik.domain.notification.NotificationType;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Slf4j
@Component
@RequiredArgsConstructor
public class VoteScheduler {

    private final VoteRepository voteRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 매일 자정에 실행되어 투표 종료 조건을 확인하고 종료 처리합니다.
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void checkVoteEndConditions() {
        LocalDate today = LocalDate.now();
        log.info("투표 종료 조건 확인 스케줄러 실행: {}", today);

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
            vote.setStatus(Vote.Status.CLOSED);
            voteRepository.save(vote);

            // 알림 생성
            createVoteClosedNotification(vote);

            log.info("투표 종료 처리 완료: {}", vote.getId());
        }
    }

    /**
     * 투표 종료 알림을 생성합니다.
     */
    private void createVoteClosedNotification(Vote vote) {
        // 모든 사용자에게 알림 생성
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Notification notification = Notification.builder()
                .userId(user.getId())
                .type(NotificationType.VOTE_CLOSED.name())
                .title("투표가 종료되었습니다")
                .content("'" + vote.getQuestion() + "' 투표가 종료되었습니다.")
                .referenceId(vote.getId())
                .read(false)
                .build();

            notificationRepository.save(notification);
        }
    }

    /**
     * 매일 자정에 실행되어 AI 사용 쿼터를 초기화합니다.
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void resetAIQuota() {
        log.info("AI 사용 쿼터 초기화 스케줄러 실행");
        // 모든 사용자의 AI 사용 쿼터 초기화
        // 이 부분은 AIQuotaService에서 구현하거나 직접 repository를 사용하여 구현할 수 있습니다.
    }
}