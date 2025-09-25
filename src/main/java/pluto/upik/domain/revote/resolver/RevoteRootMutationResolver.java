package pluto.upik.domain.revote.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.revote.data.DTO.RevoteMutation;

@Controller
@RequiredArgsConstructor
public class RevoteRootMutationResolver {

    @SchemaMapping(typeName = "Mutation", field = "revote")
    public RevoteMutation getRevoteMutation() {
        return new RevoteMutation();
    }
}