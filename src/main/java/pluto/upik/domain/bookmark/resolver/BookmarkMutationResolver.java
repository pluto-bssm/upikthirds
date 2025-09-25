package pluto.upik.domain.bookmark.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.bookmark.data.DTO.BookmarkMutation;
import pluto.upik.domain.bookmark.service.BookmarkServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BookmarkMutationResolver {

    private final BookmarkServiceInterface bookmarkService;
    private final SecurityUtil securityUtil; // 🔑 SecurityUtil 주입

    @RequireAuth
    @SchemaMapping(typeName = "BookmarkMutation", field = "toggleBookmark")
    public boolean toggleBookmark(BookmarkMutation parent, @Argument String guideId) {
        log.info("GraphQL 뮤테이션 - 북마크 토글 요청: guideId={}", guideId);
        try {
            // 현재 로그인한 사용자 ID 가져오기
            UUID userId = securityUtil.getCurrentUserId();
            UUID guideUUID = UUID.fromString(guideId);

            boolean result = bookmarkService.toggleBookmark(userId, guideUUID);
            log.info("GraphQL 뮤테이션 - 북마크 토글 완료: guideId={}, userId={}, 결과={}", guideId, userId, result ? "추가됨" : "삭제됨");
            return result;
        } catch (Exception e) {
            log.error("GraphQL 뮤테이션 - 북마크 토글 실패: guideId={}", guideId, e);
            throw new RuntimeException("북마크 토글 중 오류가 발생했습니다.", e);
        }
    }
}
