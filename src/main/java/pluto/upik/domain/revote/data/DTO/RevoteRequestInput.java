package pluto.upik.domain.revote.data.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 재투표 요청 입력 DTO
 *
 * 재투표 요청 시 필요한 입력 데이터를 담는 DTO입니다.
 *
 * @author upik-team
 * @version 2.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevoteRequestInput {

    /**
     * 재투표를 요청할 가이드 ID
     * 필수 입력값입니다.
     */
    @NotNull(message = "가이드 ID는 필수 입력값입니다.")
    private UUID guideId;

    /**
     * 재투표 요청 이유
     * 필수 입력값이며, 최대 255자까지 입력 가능합니다.
     */
    @NotBlank(message = "재투표 요청 이유는 필수 입력값입니다.")
    @Size(max = 255, message = "재투표 요청 이유는 255자를 초과할 수 없습니다.")
    private String reason;

    /**
     * 재투표 요청 상세 이유
     * 선택사항이며, 최대 1000자까지 입력 가능합니다.
     */
    @Size(max = 1000, message = "상세 이유는 1000자를 초과할 수 없습니다.")
    private String detailReason;
}