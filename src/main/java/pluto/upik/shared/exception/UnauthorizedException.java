package pluto.upik.shared.exception;

import lombok.Getter;

/**
 * 인증되지 않은 사용자의 접근 시도 시 발생하는 예외
 *
 * 로그인이 필요한 작업을 비인증 사용자가 시도할 때 발생합니다.
 * HTTP 상태 코드 401(Unauthorized)에 매핑됩니다.
 */
@Getter
public class UnauthorizedException extends RuntimeException {

    private final String errorCode;
    private final String resource;

    /**
     * 기본 생성자
     */
    public UnauthorizedException() {
        this("UNKNOWN", "인증이 필요합니다.");
    }

    /**
     * 리소스명과 메시지를 지정하는 생성자
     *
     * @param resource 접근하려던 리소스
     * @param message 예외 메시지
     */
    public UnauthorizedException(String resource, String message) {
        super(message);
        this.errorCode = "UNAUTHORIZED";
        this.resource = resource;
    }
}
