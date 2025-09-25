package pluto.upik.shared.ai.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateOptionsResponse {
    private List<String> options;
    private int remainingQuota; // 남은 AI 사용 쿼터
}