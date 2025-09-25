package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.service.ElasticSearchGuideService;
import pluto.upik.domain.guide.service.KeywordGuideServiceInterface;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.List;

/**
 * 키워드 가이드 관련 GraphQL 쿼리 리졸버
 * 키워드 기반 가이드 검색 등의 쿼리 요청을 처리합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class KeywordGuideResolver {

    private final KeywordGuideServiceInterface keywordGuideService;
    private final ElasticSearchGuideService elasticSearchGuideService;
    /**
     * 특정 키워드가 포함된 가이드 목록을 검색합니다.
     *
     * @param parent GraphQL 부모 객체
     * @param keyword 검색할 키워드
     * @return 키워드 가이드 응답 목록
     */
    @SchemaMapping(typeName = "KeywordGuideQuery", field = "searchByKeyword")
    public List<KeywordGuideResponse> searchByKeyword(Object parent, @Argument String keyword) {
        log.info("GraphQL 쿼리 - 키워드 기반 가이드 검색 요청: keyword={}", keyword);

        try {
            List<KeywordGuideResponse> guides = keywordGuideService.searchGuidesByKeyword(keyword);
            log.info("GraphQL 쿼리 - 키워드 기반 가이드 검색 완료: keyword={}, 결과 개수={}", keyword, guides.size());
            return guides;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 키워드 기반 가이드 검색 실패: keyword={}", keyword, e);
            throw e;
}
    }

    /**
     * 엘라스틱서치를 사용하여 유사 제목으로 가이드를 검색하는 GraphQL 쿼리 핸들러
     *
     * @param parent GraphQL 부모 객체
     * @param title 검색할 제목
     * @return 유사 제목 가이드 응답 목록
     */
    @SchemaMapping(typeName = "KeywordGuideQuery", field = "searchSimilarByTitle")
    public List<KeywordGuideResponse> searchSimilarByTitle(Object parent, @Argument String title) {
        log.info("GraphQL 쿼리 - 유사 제목 기반 가이드 검색 요청: title={}", title);

        try {
            elasticSearchGuideService.indexAllGuides();
            List<KeywordGuideResponse> guides = elasticSearchGuideService.searchSimilarGuidesByTitle(title);
            log.info("GraphQL 쿼리 - 유사 제목 기반 가이드 검색 완료: title={}, 결과 개수={}", title, guides.size());
            return guides;
        } catch (ResourceNotFoundException e) {
            log.warn("GraphQL 쿼리 - 유사 제목 기반 가이드 검색 실패: title={}, 오류={}", title, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 유사 제목 기반 가이드 검색 중 오류 발생: title={}", title, e);
            throw new BusinessException("유사 제목 검색 중 오류가 발생했습니다.");
}
    }
}
