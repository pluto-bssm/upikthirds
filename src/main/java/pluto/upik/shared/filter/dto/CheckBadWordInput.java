package pluto.upik.shared.filter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 욕설 검사 입력 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckBadWordInput {
    /**
     * 검사할 텍스트
     */
    private String text;
}
