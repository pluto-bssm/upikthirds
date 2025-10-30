package pluto.upik.domain.inquiry;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문의하기 요청 DTO
 * 문의 유형, 상세 내용, 답변 받을 이메일 주소를 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDTO {

    /**
     * 문의 유형
     * 예: "기술 문의", "서비스 문의", "버그 신고", "기능 제안" 등
     */
    @NotBlank(message = "문의 유형을 선택해주세요")
    private String inquiryType;

    /**
     * 상세 내용
     */
    @NotBlank(message = "문의 내용을 입력해주세요")
    private String content;

    /**
     * 답변 받을 이메일 주소
     */
    @NotBlank(message = "답변 받을 이메일 주소를 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String replyEmail;

    /**
     * 문의자 이름 (선택)
     */
    private String senderName;
}
