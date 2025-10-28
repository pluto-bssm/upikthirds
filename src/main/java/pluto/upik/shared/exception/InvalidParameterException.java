package pluto.upik.shared.exception;

import lombok.Getter;

/**
 * 잘못된 파라미터가 전달되었을 때 발생하는 예외
 *
 * 유효성 검사를 통과하지 못하거나 비즈니스 규칙에 맞지 않는 파라미터가 전달될 때 발생합니다.
 * HTTP 상태 코드 400(Bad Request)에 매핑됩니다.
 */
@Getter
public class InvalidParameterException extends RuntimeException {

    private final String errorCode;
    private final String parameterName;
    private final Object parameterValue;

    /**
     * 파라미터명과 메시지를 지정하는 생성자
     *
     * @param parameterName 잘못된 파라미터명
     * @param message 예외 메시지
     */
    public InvalidParameterException(String parameterName, String message) {
        this(parameterName, null, message);
    }

    /**
     * 파라미터명, 값, 메시지를 지정하는 생성자
     *
     * @param parameterName 잘못된 파라미터명
     * @param parameterValue 잘못된 파라미터 값
     * @param message 예외 메시지
     */
    public InvalidParameterException(String parameterName, Object parameterValue, String message) {
        super(message);
        this.errorCode = "INVALID_PARAMETER";
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }
}
