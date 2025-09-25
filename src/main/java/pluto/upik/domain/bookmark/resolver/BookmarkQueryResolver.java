package pluto.upik.domain.bookmark.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.bookmark.data.DTO.BookmarkQuery;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.bookmark.service.BookmarkServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BookmarkQueryResolver {

    private final BookmarkServiceInterface bookmarkService;
    private final SecurityUtil securityUtil; // 🔑 SecurityUtil 주입

    @RequireAuth
    @SchemaMapping(typeName = "BookmarkQuery", field = "getBookmarkedGuides")
    public List<GuideResponse> getBookmarkedGuides(BookmarkQuery parent) {
        log.info("GraphQL 쿼리 - 북마크한 가이드 목록 조회 요청");
        try {
            // 현재 로그인한 사용자 ID 가져오기
            UUID userId = securityUtil.getCurrentUserId();

            // 실제 서비스 호출
            List<GuideResponse> guides = bookmarkService.getBookmarkedGuidesByUserId(userId);
            log.info("GraphQL 쿼리 - 북마크한 가이드 목록 조회 완료: userId={}, 조회된 가이드 수={}", userId, guides.size());
            return guides;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 북마크한 가이드 목록 조회 실패", e);
            throw new RuntimeException("북마크 목록 조회 중 오류가 발생했습니다.", e);
        }
    }
}
