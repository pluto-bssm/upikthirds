package pluto.upik.shared.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.vote.service.VoteClosureService;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoteScheduler {

    private final VoteClosureService voteClosureService;

    /**
     * 매일 자정에 실행되어 투표 종료 조건을 확인하고 종료 처리합니다.
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void checkVoteEndConditions() {
        log.info("자동 투표 종료 스케줄러 실행");
        int closedCount = voteClosureService.checkAndCloseAllVotes();
        log.info("자동 투표 종료 완료: {}개 투표 종료됨", closedCount);
    }

    /**
     * 수동으로 투표 종료 조건을 확인합니다.
     * API를 통해 호출 가능합니다.
     */
    @Transactional
    public int manualCheckVoteEndConditions() {
        log.info("수동 투표 종료 체크 실행");
        int closedCount = voteClosureService.checkAndCloseAllVotes();
        log.info("수동 투표 종료 완료: {}개 투표 종료됨", closedCount);
        return closedCount;
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