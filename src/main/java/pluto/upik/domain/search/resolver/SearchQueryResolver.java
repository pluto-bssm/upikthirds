package pluto.upik.domain.search.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.search.data.DTO.SearchQuery;
import pluto.upik.domain.search.data.DTO.SearchRequest;
import pluto.upik.domain.search.data.DTO.SearchResponse;
import pluto.upik.domain.search.service.SearchServiceInterface;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchQueryResolver {
    private final SearchServiceInterface searchService;
    
    @SchemaMapping(typeName = "SearchQuery", field = "execute")
    public SearchResponse execute(
            Object parent,
            @Argument String query,
            @Argument Integer limit,
            @Argument Boolean includeAiSummary) {

        log.info("SearchQueryResolver - execute() 호출 - query: {}, limit: {}, includeAiSummary: {}",
                query, limit, includeAiSummary);

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .limit(limit)
                .includeAiSummary(includeAiSummary)
                .build();

        return searchService.search(request);
    }
}