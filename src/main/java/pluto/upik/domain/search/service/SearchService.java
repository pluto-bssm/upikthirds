package pluto.upik.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.service.ElasticSearchGuideService;
import pluto.upik.domain.guide.service.KeywordGuideService;
import pluto.upik.domain.search.data.DTO.SearchRequest;
import pluto.upik.domain.search.data.DTO.SearchResponse;
import pluto.upik.shared.ai.config.ChatAiService;
import pluto.upik.shared.ai.service.AIService;
import pluto.upik.shared.exception.BusinessException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService implements SearchServiceInterface {

    private final ElasticSearchGuideService elasticSearchGuideService;
    private final KeywordGuideService keywordGuideService;
    private final ChatAiService aiService;

    /**
     * 사용자 검색 쿼리를 처리하여 관련 가이드 문서와 AI 요약을 제공합니다.
     * 
     * @param request 검색 요청 정보를 담은 객체
     * @return 검색 결과와 AI 요약을 포함한 응답 객체
     */
    @Override
    public SearchResponse search(SearchRequest request) {
        log.info("검색 요청 처리 시작 - query: {}, includeAiSummary: {}", 
                request.getQuery(), request.getIncludeAiSummary());
        
        long startTime = System.currentTimeMillis();
        String query = request.getQuery();
        Integer limit = request.getLimit() != null ? request.getLimit() : 10;
        Boolean includeAiSummary = request.getIncludeAiSummary() != null ? request.getIncludeAiSummary() : true;
        
        try {
            // 1. ElasticSearch를 통해 가이드 문서 검색
            List<KeywordGuideResponse> guides;
            try {
                guides = elasticSearchGuideService.searchSimilarGuidesByTitle(query);
                if (guides.size() > limit) {
                    guides = guides.subList(0, limit);
                }
                log.info("ElasticSearch 검색 결과 - query: {}, 결과 수: {}", query, guides.size());
            } catch (Exception e) {
                log.warn("ElasticSearch 검색 실패, 키워드 기반 검색으로 대체 - query: {}, error: {}", 
                        query, e.getMessage());
                
                // ElasticSearch 검색 실패 시 대체 검색 방법 사용
                try {
                    guides = keywordGuideService.searchGuidesByKeyword(query);
                    if (guides.size() > limit) {
                        guides = guides.subList(0, limit);
                    }
                } catch (Exception ex) {
                    log.error("키워드 기반 검색도 실패 - query: {}, error: {}", query, ex.getMessage());
                    guides = Collections.emptyList();
                }
            }
            
            // 2. AI 요약 생성 (요청 시)
            String aiSummary = null;
            if (includeAiSummary && !guides.isEmpty()) {
                try {
                    aiSummary = generateAISummary(query, guides);
                    log.info("AI 요약 생성 완료 - query: {}", query);
                } catch (Exception e) {
                    log.error("AI 요약 생성 실패 - query: {}, error: {}", query, e.getMessage());
                    aiSummary = "요약을 생성하는 중 오류가 발생했습니다.";
                }
            }
            
            long endTime = System.currentTimeMillis();
            long searchTimeMs = endTime - startTime;
            
            // 3. 검색 응답 생성
            SearchResponse response = SearchResponse.builder()
                    .query(query)
                    .guides(guides)
                    .aiSummary(aiSummary)
                    .totalResults(guides.size())
                    .searchTimeMs((int)searchTimeMs) // Long을 Integer로 변환
                    .build();
            
            log.info("검색 요청 처리 완료 - query: {}, 결과 수: {}, 소요 시간: {}ms", 
                    query, guides.size(), searchTimeMs);
            return response;
            
        } catch (Exception e) {
            log.error("검색 처리 중 오류 발생 - query: {}, error: {}", query, e.getMessage(), e);
            throw new BusinessException("검색 처리 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 검색 쿼리와 검색 결과를 기반으로 AI 요약을 생성합니다.
     * 
     * @param query 사용자 검색 쿼리
     * @param guides 검색된 가이드 목록
     * @return AI가 생성한 요약 텍스트
     */
    private String generateAISummary(String query, List<KeywordGuideResponse> guides) {
        // 요청 식별자 생성
        String requestKey = "search-summary-" + UUID.randomUUID().toString();
        
        // 가이드 내용 추출
        String guidesContent = guides.stream()
                .map(guide -> "제목: " + guide.getTitle() + "\n내용: " + 
                      (guide.getContent().length() > 300 ? 
                       guide.getContent().substring(0, 300) + "..." : 
                       guide.getContent()))
                .collect(Collectors.joining("\n\n"));
        
        // AI 요약 요청 프롬프트 생성
        String prompt = String.format(
                "다음은 사용자의 검색어 \"%s\"와 관련된 문서들입니다. 이 문서들의 내용을 종합하여 " +
                "사용자의 검색 의도에 맞는 간결하고 유용한 요약을 500자 이내로 제공해주세요. 그리고 단순 줄글 형식으로 주세요" +
                "요약은 중요한 정보를 포함하고, 일관성 있게 작성되어야 합니다.\n\n%s",
                query, guidesContent);
        
        // AI에 요약 요청
        return aiService.askToDeepSeekAI(prompt);
    }
}