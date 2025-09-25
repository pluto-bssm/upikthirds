package pluto.upik.domain.bookmark.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.bookmark.data.DTO.BookmarkQuery;

@Controller
@RequiredArgsConstructor
public class BookmarkRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "bookmark")
    public BookmarkQuery getBookmarkQuery() {
        return new BookmarkQuery();
    }
}