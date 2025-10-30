package pluto.upik.domain.inquiry;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 문의하기 Root Query Resolver
 * Query 타입의 inquiry 필드를 InquiryQuery 타입으로 매핑
 */
@Controller
public class InquiryRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "inquiry")
    public InquiryQuery inquiry() {
        return new InquiryQuery();
    }
}
