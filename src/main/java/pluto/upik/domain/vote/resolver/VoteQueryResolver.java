//package pluto.upik.domain.vote.resolver;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.graphql.data.method.annotation.Argument;
//import org.springframework.graphql.data.method.annotation.SchemaMapping;
//// @Controller 어노테이션 제거
//import org.springframework.stereotype.Component;
//import pluto.upik.domain.vote.data.DTO.VoteDetailPayload;
//import pluto.upik.domain.vote.data.DTO.VotePayload;
//import pluto.upik.domain.vote.service.VoteService;
//import pluto.upik.shared.oauth2jwt.util.SecurityUtil;
//
//import java.util.List;
//import java.util.UUID;
//
//// @Controller 어노테이션을 제거하여 이 클래스가 GraphQL 리졸버로 등록되지 않도록 함
//// 대신 @Component를 사용하여 필요한 경우 다른 클래스에서 주입받을 수 있도록 함
//@Component
//@RequiredArgsConstructor
//public class VoteQueryResolver {
//
//    private final VoteService voteService;
//    private final SecurityUtil securityUtil;
//
//    /**
//     * 모든 투표 목록을 반환합니다.
//     *
//     * 더미 사용자 ID를 사용하여 VoteService에서 모든 투표 정보를 조회합니다.
//     *
//     * @return 전체 투표의 리스트
//     */
//    @SchemaMapping(typeName = "VoteQuery", field = "getAllVotes")
//    public List<VotePayload> getAllVotes() {
//        UUID userId = securityUtil.isAuthenticated() ? securityUtil.getCurrentUserId() : null;
//        return voteService.getAllVotes(userId);
//    }
//
//    /**
//     * 주어진 투표 ID에 해당하는 투표의 상세 정보를 반환합니다.
//     *
//     * @param id 조회할 투표의 UUID 문자열
//     * @return 해당 투표의 상세 정보 페이로드
//     */
//    @SchemaMapping(typeName = "VoteQuery", field = "getVoteById")
//    public VoteDetailPayload getVoteById(@Argument String id) {
//        UUID userId = securityUtil.isAuthenticated() ? securityUtil.getCurrentUserId() : null;
//        return voteService.getVoteById(UUID.fromString(id), userId);
//    }
//
//    /**
//     * 가장 인기 있는 오픈 투표 3개를 반환합니다.
//     *
//     * @return 인기 순으로 정렬된 오픈 투표의 리스트
//     */
//    @SchemaMapping(typeName = "VoteQuery", field = "getMostPopularOpenVote")
//    public List<VotePayload> getMostPopularOpenVote() {
//        // 인기 있는 투표 3개를 반환
//        return voteService.getMostPopularOpenVote();
//    }
//
//    /**
//     * 가장 인기가 적은 오픈 투표를 반환합니다.
//     *
//     * VoteService에서 해당 투표를 항상 투표하지 않은 상태로 처리합니다.
//     *
//     * @return 인기가 가장 적은 오픈 투표의 정보
//     */
//    @SchemaMapping(typeName = "VoteQuery", field = "getLeastPopularOpenVote")
//    public VotePayload getLeastPopularOpenVote() {
//        // 항상 투표하지 않은 것으로 표시 (VoteService에서 처리)
//        return voteService.getLeastPopularOpenVote();
//    }
//}