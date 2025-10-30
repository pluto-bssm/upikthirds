package pluto.upik.domain.inquiry;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 문의하기 이메일 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryEmailService {

    private final JavaMailSender mailSender;

    @Value("${inquiry.recipient.email}")
    private String recipientEmail;

    /**
     * 문의 이메일을 발송합니다
     *
     * @param inquiryDTO 문의 정보
     * @throws MessagingException 이메일 발송 실패 시
     */
    public void sendInquiryEmail(InquiryDTO inquiryDTO) throws MessagingException {
        log.info("문의 이메일 발송 시작 - 유형: {}, 답변 이메일: {}",
                inquiryDTO.getInquiryType(), inquiryDTO.getReplyEmail());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 받는 사람
        helper.setTo(recipientEmail);

        // 제목
        String subject = String.format("[UPIK 문의] %s - %s",
                inquiryDTO.getInquiryType(),
                inquiryDTO.getSenderName() != null ? inquiryDTO.getSenderName() : "익명");
        helper.setSubject(subject);

        // HTML 본문 생성
        String htmlContent = createHtmlContent(inquiryDTO);
        helper.setText(htmlContent, true);

        // 답장 주소 설정
        helper.setReplyTo(inquiryDTO.getReplyEmail());

        // 이메일 발송
        mailSender.send(message);
        log.info("문의 이메일 발송 완료 - 수신: {}", recipientEmail);
    }

    /**
     * 세련된 HTML 이메일 본문을 생성합니다
     */
    private String createHtmlContent(InquiryDTO inquiryDTO) {
        String currentTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss"));

        String senderInfo = inquiryDTO.getSenderName() != null ?
                inquiryDTO.getSenderName() : "익명 사용자";

        return String.format("""
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif;
                        line-height: 1.7;
                        color: #1a1a1a;
                        background: linear-gradient(135deg, #f5f7fa 0%%, #c3cfe2 100%%);
                        padding: 40px 20px;
                    }

                    .email-wrapper {
                        max-width: 650px;
                        margin: 0 auto;
                    }

                    .email-container {
                        background: #ffffff;
                        border-radius: 16px;
                        overflow: hidden;
                        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
                    }

                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        padding: 50px 40px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }

                    .header::before {
                        content: '';
                        position: absolute;
                        top: -50%%;
                        left: -50%%;
                        width: 200%%;
                        height: 200%%;
                        background: radial-gradient(circle, rgba(255,255,255,0.1) 0%%, transparent 70%%);
                        animation: pulse 15s ease-in-out infinite;
                    }

                    @keyframes pulse {
                        0%%, 100%% { transform: scale(1); }
                        50%% { transform: scale(1.1); }
                    }

                    .header h1 {
                        color: #ffffff;
                        font-size: 28px;
                        font-weight: 700;
                        margin-bottom: 8px;
                        position: relative;
                        z-index: 1;
                        letter-spacing: -0.5px;
                    }

                    .header p {
                        color: rgba(255, 255, 255, 0.9);
                        font-size: 15px;
                        position: relative;
                        z-index: 1;
                    }

                    .content {
                        padding: 45px 40px;
                    }

                    .info-card {
                        background: linear-gradient(135deg, #f8f9ff 0%%, #f0f2ff 100%%);
                        border-radius: 12px;
                        padding: 30px;
                        margin-bottom: 30px;
                        border: 1px solid #e3e8ff;
                    }

                    .info-row {
                        display: flex;
                        align-items: flex-start;
                        margin-bottom: 20px;
                        padding-bottom: 20px;
                        border-bottom: 1px solid rgba(102, 126, 234, 0.1);
                    }

                    .info-row:last-child {
                        margin-bottom: 0;
                        padding-bottom: 0;
                        border-bottom: none;
                    }

                    .info-icon {
                        width: 44px;
                        height: 44px;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        border-radius: 10px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        flex-shrink: 0;
                        margin-right: 16px;
                        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
                    }

                    .info-icon svg {
                        width: 20px;
                        height: 20px;
                        fill: white;
                    }

                    .info-content {
                        flex: 1;
                    }

                    .info-label {
                        font-size: 12px;
                        font-weight: 600;
                        color: #667eea;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        margin-bottom: 6px;
                    }

                    .info-value {
                        font-size: 15px;
                        color: #2d3748;
                        font-weight: 500;
                    }

                    .info-value a {
                        color: #667eea;
                        text-decoration: none;
                        transition: color 0.2s;
                    }

                    .info-value a:hover {
                        color: #764ba2;
                    }

                    .inquiry-type-badge {
                        display: inline-block;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 8px 20px;
                        border-radius: 20px;
                        font-size: 13px;
                        font-weight: 600;
                        letter-spacing: 0.5px;
                        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
                    }

                    .message-section {
                        margin-top: 30px;
                    }

                    .message-label {
                        font-size: 12px;
                        font-weight: 600;
                        color: #667eea;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        margin-bottom: 12px;
                    }

                    .message-box {
                        background: #ffffff;
                        border: 2px solid #e3e8ff;
                        border-radius: 12px;
                        padding: 25px;
                        font-size: 15px;
                        line-height: 1.8;
                        color: #2d3748;
                        white-space: pre-wrap;
                        word-wrap: break-word;
                        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.08);
                    }

                    .action-section {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 40px;
                        border-top: 2px solid #f0f2ff;
                    }

                    .reply-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 16px 48px;
                        text-decoration: none;
                        border-radius: 30px;
                        font-weight: 600;
                        font-size: 15px;
                        letter-spacing: 0.5px;
                        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
                        transition: all 0.3s ease;
                    }

                    .reply-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 12px 28px rgba(102, 126, 234, 0.5);
                    }

                    .footer {
                        background: #f8f9ff;
                        padding: 30px 40px;
                        text-align: center;
                        border-top: 1px solid #e3e8ff;
                    }

                    .footer-brand {
                        font-size: 18px;
                        font-weight: 700;
                        color: #667eea;
                        margin-bottom: 12px;
                    }

                    .footer-text {
                        font-size: 13px;
                        color: #718096;
                        line-height: 1.6;
                        margin: 6px 0;
                    }

                    .divider {
                        height: 1px;
                        background: linear-gradient(to right, transparent, #e3e8ff, transparent);
                        margin: 25px 0;
                    }

                    @media (max-width: 600px) {
                        .header {
                            padding: 35px 25px;
                        }

                        .header h1 {
                            font-size: 22px;
                        }

                        .content {
                            padding: 30px 25px;
                        }

                        .info-card {
                            padding: 20px;
                        }

                        .footer {
                            padding: 25px 25px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="email-wrapper">
                    <div class="email-container">
                        <div class="header">
                            <h1>새로운 문의가 접수되었습니다</h1>
                            <p>UPIK 고객 지원팀</p>
                        </div>

                        <div class="content">
                            <div class="info-card">
                                <div class="info-row">
                                    <div class="info-icon">
                                        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M12 2L2 7L12 12L22 7L12 2Z"/>
                                            <path d="M2 17L12 22L22 17"/>
                                            <path d="M2 12L12 17L22 12"/>
                                        </svg>
                                    </div>
                                    <div class="info-content">
                                        <div class="info-label">문의 유형</div>
                                        <div class="info-value">
                                            <span class="inquiry-type-badge">%s</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="info-row">
                                    <div class="info-icon">
                                        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21"/>
                                            <circle cx="12" cy="7" r="4"/>
                                        </svg>
                                    </div>
                                    <div class="info-content">
                                        <div class="info-label">문의자</div>
                                        <div class="info-value">%s</div>
                                    </div>
                                </div>

                                <div class="info-row">
                                    <div class="info-icon">
                                        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path d="M4 4H20C21.1 4 22 4.9 22 6V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V6C2 4.9 2.9 4 4 4Z"/>
                                            <polyline points="22,6 12,13 2,6"/>
                                        </svg>
                                    </div>
                                    <div class="info-content">
                                        <div class="info-label">답변 이메일</div>
                                        <div class="info-value">
                                            <a href="mailto:%s">%s</a>
                                        </div>
                                    </div>
                                </div>

                                <div class="info-row">
                                    <div class="info-icon">
                                        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <circle cx="12" cy="12" r="10"/>
                                            <polyline points="12 6 12 12 16 14"/>
                                        </svg>
                                    </div>
                                    <div class="info-content">
                                        <div class="info-label">접수 시간</div>
                                        <div class="info-value">%s</div>
                                    </div>
                                </div>
                            </div>

                            <div class="message-section">
                                <div class="message-label">문의 내용</div>
                                <div class="message-box">%s</div>
                            </div>

                            <div class="action-section">
                                <a href="mailto:%s" class="reply-button">답변하기</a>
                            </div>
                        </div>

                        <div class="footer">
                            <div class="footer-brand">UPIK</div>
                            <p class="footer-text">이 이메일은 UPIK 시스템에서 자동으로 발송되었습니다.</p>
                            <p class="footer-text">답변은 고객의 이메일 주소로 직접 전송해주세요.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            inquiryDTO.getInquiryType(),
            senderInfo,
            inquiryDTO.getReplyEmail(),
            inquiryDTO.getReplyEmail(),
            currentTime,
            inquiryDTO.getContent(),
            inquiryDTO.getReplyEmail()
        );
    }
}
