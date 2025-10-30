package pluto.upik.domain.inquiry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 문의하기 GraphQL Query Resolver
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class InquiryQueryResolver {

    /**
     * 문의 유형 목록 조회
     *
     * @return 사용 가능한 문의 유형 목록
     */
    @SchemaMapping(typeName = "InquiryQuery", field = "getInquiryTypes")
    public List<String> getInquiryTypes() {
        log.debug("문의 유형 목록 조회");
        return List.of(
                "기술 문의",
                "서비스 문의",
                "버그 신고",
                "기능 제안",
                "계정 문의",
                "기타"
        );
    }

    /**
     * 문의 서비스 상태 확인
     *
     * @return 서비스 상태
     */
    @SchemaMapping(typeName = "InquiryQuery", field = "inquiryServiceHealth")
    public ServiceHealth inquiryServiceHealth() {
        log.debug("문의 서비스 상태 확인");
        return ServiceHealth.up("inquiry");
    }
}
