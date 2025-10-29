package pluto.upik.shared.exception;

import lombok.Getter;

/**
 * 욕설이 포함된 입력이 감지되었을 때 발생하는 예외
 *
 * 사용자 입력에 부적절한 언어가 포함되어 있을 때 발생합니다.
 * HTTP 상태 코드 400(Bad Request)에 매핑됩니다.
 *
 * @author upik-team
 * @version 1.0
 * @since 2024
 */
@Getter
public class BadWordException extends BusinessException {

    private final String fieldName;

    /**
     * 기본 생성자
     */
    public BadWordException() {
        this(null, "입력하신 내용에 부적절한 언어가 포함되어 있습니다.");
    }

    /**
     * 메시지를 지정하는 생성자
     *
     * @param message 예외 메시지
     */
    public BadWordException(String message) {
        this(null, message);
    }

    /**
     * 필드명과 메시지를 지정하는 생성자
     *
     * @param fieldName 욕설이 포함된 필드명
     * @param message 예외 메시지
     */
    public BadWordException(String fieldName, String message) {
        super("BAD_WORD_DETECTED", message);
        this.fieldName = fieldName;
    }

    /**
     * 사전 정의된 욕설 예외 타입들
     */
    public static class Predefined {

        public static BadWordException inTitle() {
            return new BadWordException("title", "제목에 부적절한 언어가 포함되어 있습니다.");
        }

        public static BadWordException inContent() {
            return new BadWordException("content", "내용에 부적절한 언어가 포함되어 있습니다.");
        }

        public static BadWordException inCategory() {
            return new BadWordException("category", "카테고리에 부적절한 언어가 포함되어 있습니다.");
        }

        public static BadWordException inOptions() {
            return new BadWordException("options", "선택지에 부적절한 언어가 포함되어 있습니다.");
        }

        public static BadWordException inQuestion() {
            return new BadWordException("question", "질문에 부적절한 언어가 포함되어 있습니다.");
        }

        public static BadWordException inComment() {
            return new BadWordException("comment", "댓글에 부적절한 언어가 포함되어 있습니다.");
        }

        public static BadWordException general() {
            return new BadWordException("입력하신 내용에 부적절한 언어가 포함되어 있습니다.");
        }
    }
}
