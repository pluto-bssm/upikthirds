package pluto.upik.shared.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * 전역 예외 처리 핸들러
 * 애플리케이션에서 발생하는 모든 예외를 일관된 방식으로 처리합니다.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * ResourceNotFoundException 처리
     * 요청한 리소스(투표, 가이드, 알림 등)를 찾을 수 없는 경우
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("리소스를 찾을 수 없음 - path: {}, message: {}", path, ex.getMessage());

        // 리소스 타입별 상세 메시지 생성
        String detailedMessage = enhanceResourceNotFoundMessage(ex.getMessage(), path);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                detailedMessage,
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * BusinessException 처리
     * 비즈니스 로직 실행 중 규칙 위반이나 잘못된 요청이 발생한 경우
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("비즈니스 예외 발생 - path: {}, code: {}, message: {}",
                path, ex.getErrorCode(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getErrorCode() != null ? ex.getErrorCode() : "BUSINESS_ERROR",
                ex.getMessage(),
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 유효성 검사 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(Exception ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof BindException) {
            ((BindException) ex).getBindingResult().getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        log.error("유효성 검사 실패: {}", errors);

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "입력값 유효성 검사에 실패했습니다.",
                request.getDescription(false),
                LocalDateTime.now(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 제약 조건 위반 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (error1, error2) -> error1
                ));

        log.error("제약 조건 위반: {}", errors);

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "CONSTRAINT_VIOLATION",
                "제약 조건 위반이 발생했습니다.",
                request.getDescription(false),
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 메서드 인자 타입 불일치 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String message = String.format("파라미터 '%s'의 값 '%s'이(가) '%s' 타입으로 변환될 수 없습니다.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        log.error("인자 타입 불일치: {}", message);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "TYPE_MISMATCH",
                message,
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 접근 거부 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.error("접근 거부: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                "이 리소스에 접근할 권한이 없습니다.",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 데이터 접근 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException ex, WebRequest request) {
        log.error("데이터 접근 오류: ", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "DATA_ACCESS_ERROR",
                "데이터 처리 중 오류가 발생했습니다.",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * UnauthorizedException 처리
     * 인증되지 않은 사용자의 접근 시도
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("인증 실패 - path: {}, resource: {}, message: {}",
                path, ex.getResource(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage() + " 로그인 후 다시 시도해주세요.",
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * InvalidParameterException 처리
     * 잘못된 파라미터가 전달된 경우
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(InvalidParameterException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("잘못된 파라미터 - path: {}, parameter: {}, value: {}, message: {}",
                path, ex.getParameterName(), ex.getParameterValue(), ex.getMessage());

        String detailedMessage = String.format("파라미터 '%s'이(가) 유효하지 않습니다: %s",
                ex.getParameterName(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PARAMETER",
                detailedMessage,
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * DataAccessFailureException 처리
     * 데이터베이스 접근 실패
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(DataAccessFailureException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleDataAccessFailure(DataAccessFailureException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("데이터 접근 실패 - path: {}, operation: {}, message: {}",
                path, ex.getOperation(), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "DATA_ACCESS_FAILURE",
                "데이터베이스 접근 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.",
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * DataIntegrityViolationException 처리
     * 데이터 무결성 제약 위반 (중복 키, 외래 키 제약 등)
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("데이터 무결성 위반 - path: {}", path, ex);

        String message = "데이터 저장 중 제약 조건 위반이 발생했습니다.";
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
            message = "이미 존재하는 데이터입니다. 중복된 값을 사용할 수 없습니다.";
        } else if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
            message = "참조하는 데이터가 존재하지 않습니다.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "DATA_INTEGRITY_VIOLATION",
                message,
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * EmptyResultDataAccessException 처리
     * 데이터 조회 결과가 비어있을 때 (단일 엔티티 조회)
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleEmptyResultDataAccess(
            EmptyResultDataAccessException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("데이터 조회 결과 없음 - path: {}", path);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "EMPTY_RESULT",
                "요청한 데이터를 찾을 수 없습니다.",
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * IllegalArgumentException 처리
     * 잘못된 인자가 전달된 경우
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("잘못된 인자 - path: {}, message: {}", path, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_ARGUMENT",
                ex.getMessage() != null ? ex.getMessage() : "잘못된 요청 파라미터입니다.",
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * IllegalStateException 처리
     * 비즈니스 로직 실행 중 상태가 올바르지 않은 경우
     * (예: 투표가 종료되었거나, 이미 참여한 경우)
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("잘못된 상태 - path: {}, message: {}", path, ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_STATE",
                ex.getMessage() != null ? ex.getMessage() : "현재 요청을 처리할 수 없는 상태입니다.",
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 처리되지 않은 모든 예외 처리
     *
     * @param ex 발생한 예외
     * @param request 웹 요청
     * @return 오류 응답
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        log.error("처리되지 않은 예외 발생 - path: {}, type: {}",
                path, ex.getClass().getSimpleName(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.",
                path,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ResourceNotFoundException 메시지를 리소스 타입별로 상세화
     *
     * @param originalMessage 원본 에러 메시지
     * @param path 요청 경로
     * @return 상세화된 에러 메시지
     */
    private String enhanceResourceNotFoundMessage(String originalMessage, String path) {
        if (originalMessage == null) {
            return "요청한 리소스를 찾을 수 없습니다.";
        }

        // 리소스 타입 감지 및 상세 메시지 생성
        if (originalMessage.contains("투표") || path.contains("vote")) {
            return originalMessage + " 투표가 삭제되었거나 존재하지 않는 ID입니다.";
        } else if (originalMessage.contains("가이드") || path.contains("guide")) {
            return originalMessage + " 가이드가 삭제되었거나 존재하지 않는 ID입니다.";
        } else if (originalMessage.contains("알림") || path.contains("notification")) {
            return originalMessage + " 알림이 삭제되었거나 존재하지 않는 ID입니다.";
        } else if (originalMessage.contains("신고") || path.contains("report")) {
            return originalMessage + " 신고 내역이 존재하지 않거나 이미 처리되었습니다.";
        } else if (originalMessage.contains("사용자") || originalMessage.contains("유저") || path.contains("user")) {
            return originalMessage + " 사용자 정보를 찾을 수 없습니다.";
        } else if (originalMessage.contains("옵션") || path.contains("option")) {
            return originalMessage + " 투표 선택지가 존재하지 않습니다.";
        } else if (originalMessage.contains("테일") || path.contains("tail")) {
            return originalMessage + " 테일 질문이 존재하지 않거나 삭제되었습니다.";
        } else {
            return originalMessage;
        }
    }

    /**
     * 오류 응답 데이터 구조
     */
    public static class ErrorResponse {
        private final int status;
        private final String code;
        private final String message;
        private final String path;
        private final LocalDateTime timestamp;

        public ErrorResponse(int status, String code, String message, String path, LocalDateTime timestamp) {
            this.status = status;
            this.code = code;
            this.message = message;
            this.path = path;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /**
     * 유효성 검사 오류 응답 데이터 구조
     */
    public static class ValidationErrorResponse extends ErrorResponse {
        private final Map<String, String> errors;

        public ValidationErrorResponse(int status, String code, String message, String path,
                                      LocalDateTime timestamp, Map<String, String> errors) {
            super(status, code, message, path, timestamp);
            this.errors = errors;
}

        public Map<String, String> getErrors() {
            return errors;
        }
    }
}
