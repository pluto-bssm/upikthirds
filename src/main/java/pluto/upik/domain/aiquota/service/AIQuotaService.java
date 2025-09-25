package pluto.upik.domain.aiquota.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.aiquota.data.DTO.AIQuotaResponse;
import pluto.upik.domain.aiquota.data.model.AIUsageQuota;
import pluto.upik.domain.aiquota.repository.AIUsageQuotaRepository;
import pluto.upik.shared.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIQuotaService implements AIQuotaServiceInterface {

    private final AIUsageQuotaRepository aiUsageQuotaRepository;
    private static final int DAILY_QUOTA_LIMIT = 3; // 하루 최대 사용 횟수

    /**
     * 사용자의 AI 사용 쿼터 정보를 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return AI 사용 쿼터 정보
     */
    @Override
    @Transactional(readOnly = true)
    public AIQuotaResponse getUserQuota(UUID userId) {
        AIUsageQuota quota = getOrCreateQuota(userId);
        checkAndResetIfNeeded(quota);

        return AIQuotaResponse.builder()
                .usageCount(quota.getUsageCount())
                .remainingCount(DAILY_QUOTA_LIMIT - quota.getUsageCount())
                .lastResetDate(quota.getLastResetDate())
                            .build();
    }

    /**
     * 사용자의 AI 사용 쿼터를 증가시킵니다.
     *
     * @param userId 사용자 ID
     * @return 업데이트된 AI 사용 쿼터 정보
     */
    @Override
    @Transactional
    public AIQuotaResponse incrementUsage(UUID userId) {
        AIUsageQuota quota = getOrCreateQuota(userId);
        checkAndResetIfNeeded(quota);

        if (quota.getUsageCount() >= DAILY_QUOTA_LIMIT) {
            throw new BusinessException("일일 AI 사용 한도를 초과했습니다. 내일 다시 시도해주세요.");
        }

        quota.setUsageCount(quota.getUsageCount() + 1);
        aiUsageQuotaRepository.save(quota);

        return AIQuotaResponse.builder()
                .usageCount(quota.getUsageCount())
                .remainingCount(DAILY_QUOTA_LIMIT - quota.getUsageCount())
                .lastResetDate(quota.getLastResetDate())
                .build();
    }

    /**
     * 모든 사용자의 AI 사용 쿼터를 초기화합니다.
     */
    @Override
    @Transactional
    public void resetAllQuotas() {
        LocalDate today = LocalDate.now();
        List<AIUsageQuota> allQuotas = aiUsageQuotaRepository.findAll();

        for (AIUsageQuota quota : allQuotas) {
            quota.setUsageCount(0);
            quota.setLastResetDate(today);
            aiUsageQuotaRepository.save(quota);
        }

        log.info("모든 사용자의 AI 사용 쿼터가 초기화되었습니다. 날짜: {}", today);
    }

    /**
     * AI 쿼터를 사용합니다. incrementUsage와 동일한 기능을 수행합니다.
     *
     * @param userId 사용자 ID
     * @return 업데이트된 AI 사용 쿼터 정보
     */
    @Override
    @Transactional
    public AIQuotaResponse useAIQuota(UUID userId) {
        return incrementUsage(userId);
    }
    /**
     * 특정 사용자의 AI 사용 쿼터를 초기화합니다.
     *
     * @param userId 사용자 ID
     * @return 초기화된 AI 사용 쿼터 정보
     */
    @Override
    @Transactional
    public AIQuotaResponse resetAIQuota(UUID userId) {
        AIUsageQuota quota = getOrCreateQuota(userId);
            quota.setUsageCount(0);
        quota.setLastResetDate(LocalDate.now());
            aiUsageQuotaRepository.save(quota);

        log.info("사용자 {}의 AI 사용 쿼터가 초기화되었습니다.", userId);

        return AIQuotaResponse.builder()
                .usageCount(quota.getUsageCount())
                .remainingCount(DAILY_QUOTA_LIMIT - quota.getUsageCount())
                .lastResetDate(quota.getLastResetDate())
                .build();
        }
    /**
     * 사용자의 AI 쿼터를 지정된 양만큼 증가시킵니다.
     *
     * @param userId 사용자 ID
     * @param amount 증가시킬 쿼터 양
     * @return 업데이트된 AI 사용 쿼터 정보
     */
    @Override
    @Transactional
    public AIQuotaResponse increaseAIQuota(UUID userId, int amount) {
        if (amount <= 0) {
            throw new BusinessException("증가시킬 쿼터 양은 0보다 커야 합니다.");
}

        AIUsageQuota quota = getOrCreateQuota(userId);
        checkAndResetIfNeeded(quota);

        // 음수가 되지 않도록 보장
        int newUsageCount = Math.max(0, quota.getUsageCount() - amount);
        quota.setUsageCount(newUsageCount);
        aiUsageQuotaRepository.save(quota);

        log.info("사용자 {}의 AI 쿼터가 {}만큼 증가되었습니다.", userId, amount);

        return AIQuotaResponse.builder()
                .usageCount(quota.getUsageCount())
                .remainingCount(DAILY_QUOTA_LIMIT - quota.getUsageCount())
                .lastResetDate(quota.getLastResetDate())
                .build();
    }

    /**
     * 사용자의 AI 사용 쿼터를 가져오거나 없으면 새로 생성합니다.
     *
     * @param userId 사용자 ID
     * @return AI 사용 쿼터
     */
    private AIUsageQuota getOrCreateQuota(UUID userId) {
        return aiUsageQuotaRepository.findById(userId)
                .orElseGet(() -> {
                    AIUsageQuota newQuota = AIUsageQuota.builder()
                            .userId(userId)
                            .usageCount(0)
                            .lastResetDate(LocalDate.now())
                            .build();
                    return aiUsageQuotaRepository.save(newQuota);
                });
    }

    /**
     * 필요한 경우 쿼터를 초기화합니다.
     *
     * @param quota 확인할 쿼터
     */
    private void checkAndResetIfNeeded(AIUsageQuota quota) {
        LocalDate today = LocalDate.now();
        if (quota.getLastResetDate() == null || !quota.getLastResetDate().equals(today)) {
            quota.setUsageCount(0);
            quota.setLastResetDate(today);
            aiUsageQuotaRepository.save(quota);
        }
    }
}
