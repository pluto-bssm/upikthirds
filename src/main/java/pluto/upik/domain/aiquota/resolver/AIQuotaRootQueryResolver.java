package pluto.upik.domain.aiquota.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.aiquota.data.DTO.AIQuotaQuery;

@Controller
@RequiredArgsConstructor
public class AIQuotaRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "aiQuota")
    public AIQuotaQuery getAIQuotaQuery() {
        return new AIQuotaQuery();
    }
}