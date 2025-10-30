package pluto.upik.domain.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GraphQL 문의 입력 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryInput {
    private String inquiryType;
    private String content;
    private String replyEmail;
    private String senderName;
}
