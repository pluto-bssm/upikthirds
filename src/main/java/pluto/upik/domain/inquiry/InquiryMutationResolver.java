package pluto.upik.domain.inquiry;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 문의하기 GraphQL Mutation Resolver
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class InquiryMutationResolver {

    private final InquiryEmailService emailService;

    /**
     * 문의 이메일 발송 Mutation
     *
     * @param input 문의 입력 데이터
     * @return 발송 결과
     */
    @SchemaMapping(typeName = "InquiryMutation", field = "sendInquiry")
    public InquiryResponse sendInquiry(@Argument("input") InquiryInput input) {
        log.info("GraphQL 문의 요청 - 유형: {}, 답변 이메일: {}",
                input.getInquiryType(), input.getReplyEmail());

        // 입력 유효성 검사
        if (input.getInquiryType() == null || input.getInquiryType().trim().isEmpty()) {
            log.warn("문의 유형이 비어있음");
            return InquiryResponse.failure("문의 유형을 선택해주세요", "INQUIRY_TYPE_REQUIRED");
        }

        if (input.getContent() == null || input.getContent().trim().isEmpty()) {
            log.warn("문의 내용이 비어있음");
            return InquiryResponse.failure("문의 내용을 입력해주세요", "CONTENT_REQUIRED");
        }

        if (input.getReplyEmail() == null || input.getReplyEmail().trim().isEmpty()) {
            log.warn("답변 이메일이 비어있음");
            return InquiryResponse.failure("답변 받을 이메일 주소를 입력해주세요", "REPLY_EMAIL_REQUIRED");
        }

        // 이메일 형식 검증
        if (!isValidEmail(input.getReplyEmail())) {
            log.warn("잘못된 이메일 형식: {}", input.getReplyEmail());
            return InquiryResponse.failure("올바른 이메일 형식이 아닙니다", "INVALID_EMAIL_FORMAT");
        }

        try {
            // DTO 변환
            InquiryDTO inquiryDTO = InquiryDTO.builder()
                    .inquiryType(input.getInquiryType())
                    .content(input.getContent())
                    .replyEmail(input.getReplyEmail())
                    .senderName(input.getSenderName())
                    .build();

            // 이메일 발송
            emailService.sendInquiryEmail(inquiryDTO);

            log.info("문의 이메일 발송 성공 - 유형: {}, 답변 이메일: {}",
                    input.getInquiryType(), input.getReplyEmail());

            return InquiryResponse.success("문의가 성공적으로 접수되었습니다. 빠른 시일 내에 답변 드리겠습니다.");

        } catch (MessagingException e) {
            log.error("문의 이메일 발송 실패", e);
            return InquiryResponse.failure(
                    "이메일 발송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                    e.getMessage()
            );
        } catch (Exception e) {
            log.error("문의 처리 중 예상치 못한 오류 발생", e);
            return InquiryResponse.failure(
                    "문의 처리 중 오류가 발생했습니다.",
                    e.getMessage()
            );
        }
    }

    /**
     * 이메일 형식 검증
     */
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
