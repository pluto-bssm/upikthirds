package pluto.upik.domain.voteResponse.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.voteResponse.application.VoteResponseApplication;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VoteResponseQueryResolver {

    private final VoteResponseApplication voteResponseApplication;
    private final SecurityUtil securityUtil;

    @SchemaMapping(typeName = "VoteResponseQuery", field = "getVoteResponseCount")
    public Integer getVoteResponseCount(@Argument UUID voteId) {
        return voteResponseApplication.getVoteResponseCount(voteId).intValue();
    }

    @SchemaMapping(typeName = "VoteResponseQuery", field = "getOptionResponseCount")
    public Integer getOptionResponseCount(@Argument UUID optionId) {
        return voteResponseApplication.getOptionResponseCount(optionId).intValue();
    }

    @RequireAuth
    @SchemaMapping(typeName = "VoteResponseQuery", field = "hasUserVoted")
    public Boolean hasUserVoted(@Argument UUID voteId) {
        UUID userId = securityUtil.getCurrentUserId();
        return voteResponseApplication.hasUserVoted(userId, voteId);
    }
}
