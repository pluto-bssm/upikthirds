package pluto.upik.domain.voteResponse.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.voteResponse.application.VoteResponseApplication;
import pluto.upik.domain.voteResponse.data.DTO.CreateVoteResponseInput;
import pluto.upik.domain.voteResponse.data.DTO.VoteResponsePayload;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VoteResponseMutationResolver {

    private final VoteResponseApplication voteResponseApplication;
    private final SecurityUtil securityUtil;

    @RequireAuth
    @SchemaMapping(typeName = "VoteResponseMutation", field = "createVoteResponse")
    public VoteResponsePayload createVoteResponse(@Argument CreateVoteResponseInput input) {
        UUID userId = securityUtil.getCurrentUserId();
        return voteResponseApplication.createVoteResponse(input, userId);
    }
}
