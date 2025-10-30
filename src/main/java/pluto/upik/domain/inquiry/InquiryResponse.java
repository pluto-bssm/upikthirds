package pluto.upik.domain.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GraphQL 문의 응답 타입
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponse {
    private Boolean success;
    private String message;
    private String error;

    public static InquiryResponse success(String message) {
        return InquiryResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static InquiryResponse failure(String message, String error) {
        return InquiryResponse.builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
}
