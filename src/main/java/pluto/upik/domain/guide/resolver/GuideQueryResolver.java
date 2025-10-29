package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuideQuery;
import pluto.upik.domain.guide.data.DTO.GuidePage;
import pluto.upik.domain.guide.service.GuideQueryServiceInterface;
import java.util.UUID;

/**
 * 가이드 관련 GraphQL 쿼리 리졸버
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GuideQueryResolver {

    private final GuideQueryServiceInterface guideQueryService;
    /**
     * 특정 ID를 가진 가이드의 상세 정보를 조회합니다.
     *
     * @param id 조회할 가이드의 ID
     * @return 가이드 상세 정보를 담은 GuideDetailResponse
     */
    @SchemaMapping(typeName = "GuideQuery", field = "guideById")
    public GuideDetailResponse guideById(@Argument String id) {
        log.info("guideById 쿼리 요청 - id: {}", id);
        try {
            UUID guideId = UUID.fromString(id);
            return guideQueryService.findGuideById(guideId);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 가이드 ID 형식 - id: {}", id);
            throw new IllegalArgumentException("잘못된 가이드 ID 형식입니다.");
        }
    }

    /**
     * 모든 가이드를 페이징하여 조회합니다.
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 기준 ("date" 또는 "bookmark")
     * @return 페이징된 가이드 정보
     */
    @SchemaMapping(typeName = "GuideQuery", field = "getAllGuides")
    public GuidePage getAllGuides(@Argument int page, @Argument int size, @Argument String sortBy) {
        log.info("getAllGuides 쿼리 요청 - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        return guideQueryService.getAllGuides(page, size, sortBy);
    }
}