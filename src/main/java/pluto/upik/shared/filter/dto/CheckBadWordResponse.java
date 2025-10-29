package pluto.upik.shared.filter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 욕설 검사 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckBadWordResponse {
    /**
     * 욕설 포함 여부
     */
    private Boolean containsBadWord;

    /**
     * 응답 메시지
     */
    private String message;

    /**
     * 검사한 텍스트
     */
    private String checkedText;

    /**
     * 욕설이 감지된 경우 생성하는 팩토리 메서드
     *
     * @param text 검사한 텍스트
     * @return 욕설 감지 응답
     */
    public static CheckBadWordResponse detected(String text) {
        return CheckBadWordResponse.builder()
                .containsBadWord(true)
                .message("입력하신 내용에 부적절한 언어가 포함되어 있습니다.")
                .checkedText(text)
                .build();
    }

    /**
     * 욕설이 감지되지 않은 경우 생성하는 팩토리 메서드
     *
     * @param text 검사한 텍스트
     * @return 욕설 미감지 응답
     */
    public static CheckBadWordResponse clean(String text) {
        return CheckBadWordResponse.builder()
                .containsBadWord(false)
                .message("적절한 내용입니다.")
                .checkedText(text)
                .build();
    }
}
