package pluto.upik.domain.aiquota.service;

import pluto.upik.domain.aiquota.data.DTO.AIQuotaResponse;

import java.util.UUID;

public interface AIQuotaServiceInterface {
    AIQuotaResponse getUserQuota(UUID userId);
    AIQuotaResponse incrementUsage(UUID userId);
    void resetAllQuotas();

    // 추가된 메소드들
    AIQuotaResponse useAIQuota(UUID userId);
    AIQuotaResponse resetAIQuota(UUID userId);
    AIQuotaResponse increaseAIQuota(UUID userId, int amount);
}