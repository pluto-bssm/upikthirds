package pluto.upik.shared.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 실행 중 발생하는 예외
 *
 * 애플리케이션의 비즈니스 규칙 위반이나 잘못된 요청 시 발생하는 예외입니다.
 * HTTP 상태 코드 400(Bad Request)에 매핑됩니다.
 *
 * @author upik-team
 * @version 2.0
 * @since 2024
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final Object[] messageArgs;

    /**
     * 기본 생성자
     */
    public BusinessException() {
        this("BUSINESS_ERROR", "비즈니스 로직 처리 중 오류가 발생했습니다.");
    }

    /**
     * 메시지를 지정하는 생성자
     *
     * @param message 예외 메시지
     */
    public BusinessException(String message) {
        this("BUSINESS_ERROR", message);
    }

    /**
     * 에러 코드와 메시지를 지정하는 생성자
     *
     * @param errorCode 에러 코드
     * @param message 예외 메시지
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.messageArgs = null;
    }

    /**
     * 에러 코드와 메시지, 메시지 인자를 지정하는 생성자
     *
     * @param errorCode 에러 코드
     * @param message 예외 메시지 템플릿
     * @param messageArgs 메시지 인자들
     */
    public BusinessException(String errorCode, String message, Object... messageArgs) {
        super(message);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    /**
     * 메시지와 원인을 지정하는 생성자
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public BusinessException(String message, Throwable cause) {
        this("BUSINESS_ERROR", message, cause);
    }

    /**
     * 에러 코드, 메시지, 원인을 지정하는 생성자
     *
     * @param errorCode 에러 코드
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.messageArgs = null;
    }

    /**
     * 사전 정의된 비즈니스 예외 타입들
     */
    public static class Predefined {

        public static BusinessException duplicateRevoteRequest() {
            return new BusinessException("DUPLICATE_REVOTE_REQUEST",
                    "이미 해당 가이드에 대한 재투표를 요청하셨습니다.");
        }

        public static BusinessException invalidGuideId() {
            return new BusinessException("INVALID_GUIDE_ID",
                    "올바르지 않은 가이드 ID 형식입니다.");
        }

        public static BusinessException unauthorized() {
            return new BusinessException("UNAUTHORIZED",
                    "해당 작업을 수행할 권한이 없습니다.");
        }

        public static BusinessException revoteReasonRequired() {
            return new BusinessException("REVOTE_REASON_REQUIRED",
                    "재투표 요청 이유는 필수 입력값입니다.");
        }

        public static BusinessException revoteReasonTooLong() {
            return new BusinessException("REVOTE_REASON_TOO_LONG",
                    "재투표 요청 이유는 255자를 초과할 수 없습니다.");
        }

        public static BusinessException revoteDetailReasonTooLong() {
            return new BusinessException("REVOTE_DETAIL_REASON_TOO_LONG",
                    "상세 이유는 1000자를 초과할 수 없습니다.");
        }

        public static BusinessException invalidRevoteRequestStatus() {
            return new BusinessException("INVALID_REVOTE_REQUEST_STATUS",
                    "올바르지 않은 재투표 요청 상태입니다.");
        }
    }
}