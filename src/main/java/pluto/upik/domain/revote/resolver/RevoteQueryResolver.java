package pluto.upik.domain.revote.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.revote.data.DTO.RevoteQuery;
import pluto.upik.domain.revote.service.RevoteServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RevoteQueryResolver {

    private final RevoteServiceInterface revoteService;
    private final SecurityUtil securityUtil;

    @RequireAuth
    @SchemaMapping(typeName = "RevoteQuery", field = "getRevotesByGuideId")
    public List<Object> getRevotesByGuideId(RevoteQuery parent, @Argument String guideId, CustomOAuth2User oAuth2User) {
        log.info("GraphQL 쿼리 - 가이드별 재투표 조회 요청: guideId={}", guideId);
        try {
            // SecurityUtil을 통해 현재 로그인한 사용자 ID 가져오기
            UUID userId = securityUtil.getCurrentUserId();
            UUID guideUUID = UUID.fromString(guideId);
            // 더미 구현: 빈 목록 반환
            List<Object> revotes = new ArrayList<>();
            log.info("GraphQL 쿼리 - 가이드별 재투표 조회 완료: guideId={}, 조회된 재투표 수={}", guideId, revotes.size());
            return revotes;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 가이드별 재투표 조회 실패: guideId={}", guideId, e);
            throw e;
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "RevoteQuery", field = "hasUserRevotedGuide")
    public boolean hasUserRevotedGuide(RevoteQuery parent, @Argument String guideId, CustomOAuth2User oAuth2User) {
        log.info("GraphQL 쿼리 - 사용자 재투표 여부 확인 요청: guideId={}", guideId);
        try {
            // SecurityUtil을 통해 현재 로그인한 사용자 ID 가져오기
            UUID userId = securityUtil.getCurrentUserId();
            UUID guideUUID = UUID.fromString(guideId);
            // 더미 구현: 항상 false 반환
            boolean hasRevoted = false;
            log.info("GraphQL 쿼리 - 사용자 재투표 여부 확인 완료: guideId={}, 결과={}", guideId, hasRevoted);
            return hasRevoted;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 사용자 재투표 여부 확인 실패: guideId={}", guideId, e);
            throw e;
        }
    }
}
