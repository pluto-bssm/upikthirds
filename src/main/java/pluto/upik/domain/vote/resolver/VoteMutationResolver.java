package pluto.upik.domain.vote.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.vote.application.VoteApplication;
import pluto.upik.domain.vote.data.DTO.CreateVoteInput;
import pluto.upik.domain.vote.data.DTO.VotePayload;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;
import pluto.upik.shared.scheduler.VoteScheduler;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VoteMutationResolver {

    private final VoteApplication voteApplication;
    private final SecurityUtil securityUtil;
    private final VoteScheduler voteScheduler;

    @RequireAuth
    @SchemaMapping(typeName = "VoteMutation", field = "createVote")
    public VotePayload createVote(@Argument CreateVoteInput input) {
        UUID userId = securityUtil.getCurrentUserId();
        return voteApplication.createVote(input,userId);
    }

    /**
     * 수동으로 투표 종료 스케줄러를 실행합니다.
     * 종료 조건을 만족하는 모든 투표를 즉시 종료 처리합니다.
     */
    @RequireAuth
    @SchemaMapping(typeName = "VoteMutation", field = "triggerVoteClosureCheck")
    public VoteClosureResult triggerVoteClosureCheck() {
        int closedCount = voteScheduler.manualCheckVoteEndConditions();
        return new VoteClosureResult(closedCount, "투표 종료 체크 완료");
    }

    /**
     * 투표 종료 결과 응답 타입
     */
    public static class VoteClosureResult {
        private final int closedVoteCount;
        private final String message;

        public VoteClosureResult(int closedVoteCount, String message) {
            this.closedVoteCount = closedVoteCount;
            this.message = message;
        }

        public int getClosedVoteCount() {
            return closedVoteCount;
        }

        public String getMessage() {
            return message;
        }
    }
}