package pluto.upik.domain.vote.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.vote.data.DTO.VoteDetailPayload;
import pluto.upik.domain.vote.data.DTO.VotePayload;
import pluto.upik.domain.vote.service.VoteServiceUpdated;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.service.UserService;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VoteQueryResolverUpdated {

    private final VoteServiceUpdated voteService;
    private final SecurityUtil securityUtil;

    /**
     * 모든 투표 목록을 반환합니다.
     *
     * 현재 사용자 ID를 사용하여 해당 사용자의 모든 투표 정보를 조회합니다.
     *
     * @param includeExpired 종료된 투표 포함 여부 (기본값: true)
     * @return 모든 투표의 리스트
     */
    @RequireAuth
    @SchemaMapping(typeName = "VoteQuery", field = "getAllVotes")
    public List<VotePayload> getAllVotes(@Argument(name = "includeExpired") Boolean includeExpired) {
        UUID userId = securityUtil.getCurrentUserId();
        boolean include = (includeExpired != null) ? includeExpired : true;
        return voteService.getAllVotes(userId, include);
    }

    /**
     * 주어진 투표 ID에 해당하는 투표의 상세 정보를 반환합니다.
     *
     * @param id 조회할 투표의 UUID 문자열
     * @return 해당 투표의 상세 정보 페이로드
     */
    @SchemaMapping(typeName = "VoteQuery", field = "getVoteById")
    public VoteDetailPayload getVoteById(@Argument String id) {
        UUID userId = securityUtil.getCurrentUserId();
        System.out.println("userId = " + userId);
        return voteService.getVoteById(UUID.fromString(id), userId);
    }

    /**
     * 가장 인기 있는 투표 3개를 반환합니다.
     *
     * @param includeExpired 종료된 투표 포함 여부 (기본값: false)
     * @return 인기 순으로 정렬된 상위 3개의 투표 목록
     */
    @SchemaMapping(typeName = "VoteQuery", field = "getMostPopularOpenVote")
    public List<VotePayload> getMostPopularOpenVote(@Argument(name = "includeExpired") Boolean includeExpired) {
        boolean include = (includeExpired != null) ? includeExpired : false;
        return voteService.getMostPopularOpenVote(include);
    }

    /**
     * 가장 인기가 낮은 투표를 반환합니다.
     *
     * 서비스 계층에서 항상 투표하지 않은 상태로 표시됩니다.
     *
     * @param includeExpired 종료된 투표 포함 여부 (기본값: false)
     * @return 인기가 가장 낮은 투표의 정보
     */
    @SchemaMapping(typeName = "VoteQuery", field = "getLeastPopularOpenVote")
    public VotePayload getLeastPopularOpenVote(@Argument(name = "includeExpired") Boolean includeExpired) {
        boolean include = (includeExpired != null) ? includeExpired : false;
        return voteService.getLeastPopularOpenVote(include);
    }

    /**
     * 현재 사용자가 생성한 투표 목록을 반환합니다.
     *
     * @param includeExpired 종료된 투표 포함 여부 (기본값: true)
     * @return 사용자가 생성한 투표의 목록
     */
    @SchemaMapping(typeName = "VoteQuery", field = "getMyVotes")
    public List<VotePayload> getMyVotes(@Argument(name = "includeExpired") Boolean includeExpired) {
        UUID userId = securityUtil.getCurrentUserId();
        boolean include = (includeExpired != null) ? includeExpired : true;
        return voteService.getMyVotes(userId, include);
    }
}
