package pluto.upik.domain.board.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.board.data.DTO.BoardMutation;

@Controller
@RequiredArgsConstructor
public class BoardRootMutationResolver {

    @SchemaMapping(typeName = "Mutation", field = "board")
    public BoardMutation board() {
        return new BoardMutation();
    }
}