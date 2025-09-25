package pluto.upik.domain.search.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.search.data.DTO.SearchQuery;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "search")
    public SearchQuery search() {
        log.info("SearchRootQueryResolver - search() 호출");
        return new SearchQuery();
    }
}