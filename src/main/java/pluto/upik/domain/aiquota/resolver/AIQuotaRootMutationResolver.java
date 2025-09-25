package pluto.upik.domain.aiquota.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.aiquota.data.DTO.AIQuotaMutation;

@Controller
@RequiredArgsConstructor
public class AIQuotaRootMutationResolver {

    @SchemaMapping(typeName = "Mutation", field = "aiQuota")
    public AIQuotaMutation getAIQuotaMutation() {
        return new AIQuotaMutation();
    }
}