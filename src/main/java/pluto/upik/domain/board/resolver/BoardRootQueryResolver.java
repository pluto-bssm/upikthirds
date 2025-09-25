package pluto.upik.domain.board.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.board.data.DTO.BoardQuery;

@Controller
@RequiredArgsConstructor
public class BoardRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "board")
    public BoardQuery board() {
        return new BoardQuery();
    }
}