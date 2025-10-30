package pluto.upik.domain.inquiry;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 문의하기 Root Mutation Resolver
 * Mutation 타입의 inquiry 필드를 InquiryMutation 타입으로 매핑
 */
@Controller
public class InquiryRootMutationResolver {

    @SchemaMapping(typeName = "Mutation", field = "inquiry")
    public InquiryMutation inquiry() {
        return new InquiryMutation();
    }
}
