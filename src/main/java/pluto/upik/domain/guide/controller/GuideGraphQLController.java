package pluto.upik.domain.guide.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuidePage;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.service.GuideQueryServiceInterface;

import java.util.List;
import java.util.UUID;

/**
 * 가이드 관련 GraphQL 컨트롤러
 * Spring for GraphQL의 @QueryMapping을 사용하여 구현
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GuideGraphQLController {

    private final GuideQueryServiceInterface guideQueryService;

    /**
     * 모든 가이드를 페이징하여 조회합니다.
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 기준 ("date" 또는 "bookmark")
     * @return 페이징된 가이드 정보
     */
    @QueryMapping
    public GuidePage getAllGuides(
            @Argument(name = "page") Integer page,
            @Argument(name = "size") Integer size,
            @Argument(name = "sortBy") String sortBy) {
        log.info("getAllGuides 쿼리 요청 - page: {}, size: {}, sortBy: {}", page, size, sortBy);
        return guideQueryService.getAllGuides(
                page != null ? page : 0,
                size != null ? size : 10,
                sortBy != null ? sortBy : "date");
    }

    /**
     * 특정 ID를 가진 가이드의 상세 정보를 조회합니다.
     *
     * @param id 조회할 가이드의 ID
     * @return 가이드 상세 정보
     */
    @QueryMapping
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
     * 특정 카테고리의 모든 가이드를 조회합니다.
     *
     * @param category 조회할 가이드의 카테고리
     * @return 해당 카테고리의 가이드 목록
     */
    @QueryMapping
    public List<GuideResponse> guidesByCategory(@Argument String category) {
        log.info("guidesByCategory 쿼리 요청 - category: {}", category);
        return guideQueryService.findByCategory(category);
    }
}