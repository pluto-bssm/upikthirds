package pluto.upik.domain.aiquota.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pluto.upik.domain.aiquota.data.model.AIUsageQuota;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIQuotaResponse {
    private int usageCount;
    private int maxUsageCount;
    private int remainingCount;
    private LocalDate lastResetDate;
    private boolean canUseNow;

    public static AIQuotaResponse fromEntity(AIUsageQuota quota) {
        final int MAX_USAGE_COUNT = 3;
        int remaining = Math.max(0, MAX_USAGE_COUNT - quota.getUsageCount());

        return AIQuotaResponse.builder()
                .usageCount(quota.getUsageCount())
                .maxUsageCount(MAX_USAGE_COUNT)
                .remainingCount(remaining)
                .lastResetDate(quota.getLastResetDate())
                .canUseNow(remaining > 0)
                .build();
    }
}