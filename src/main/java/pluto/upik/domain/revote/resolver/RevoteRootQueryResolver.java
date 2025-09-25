package pluto.upik.domain.revote.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.revote.data.DTO.RevoteQuery;

@Controller
@RequiredArgsConstructor
public class RevoteRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "revote")
    public RevoteQuery getRevoteQuery() {
        return new RevoteQuery();
    }
}