package pluto.upik.shared.exception;

import lombok.Getter;

/**
 * 데이터베이스 접근 실패 시 발생하는 예외
 *
 * 데이터베이스 연결 문제, 쿼리 실행 실패 등 데이터 접근 계층의 문제를 나타냅니다.
 * HTTP 상태 코드 503(Service Unavailable)에 매핑됩니다.
 */
@Getter
public class DataAccessFailureException extends RuntimeException {

    private final String errorCode;
    private final String operation;

    /**
     * 작업명과 메시지를 지정하는 생성자
     *
     * @param operation 수행하려던 작업
     * @param message 예외 메시지
     */
    public DataAccessFailureException(String operation, String message) {
        super(message);
        this.errorCode = "DATA_ACCESS_FAILURE";
        this.operation = operation;
    }

    /**
     * 작업명, 메시지, 원인을 지정하는 생성자
     *
     * @param operation 수행하려던 작업
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public DataAccessFailureException(String operation, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DATA_ACCESS_FAILURE";
        this.operation = operation;
    }
}
