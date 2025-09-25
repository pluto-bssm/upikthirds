package pluto.upik.domain.bookmark.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.bookmark.data.DTO.BookmarkMutation;

@Controller
@RequiredArgsConstructor
public class BookmarkRootMutationResolver {

    @SchemaMapping(typeName = "Mutation", field = "bookmark")
    public BookmarkMutation getBookmarkMutation() {
        return new BookmarkMutation();
    }
}