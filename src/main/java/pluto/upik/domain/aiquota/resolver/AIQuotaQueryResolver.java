package pluto.upik.domain.aiquota.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.aiquota.data.DTO.AIQuotaQuery;
import pluto.upik.domain.aiquota.data.DTO.AIQuotaResponse;
import pluto.upik.domain.aiquota.service.AIQuotaServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AIQuotaQueryResolver {

    private final AIQuotaServiceInterface aiQuotaService;
    private final SecurityUtil securityUtil;

    @RequireAuth
    @SchemaMapping(typeName = "AIQuotaQuery", field = "getRemainingQuota")
    public int getRemainingQuota(AIQuotaQuery source) {
        log.info("GraphQL 쿼리 - AI 쿼터 잔여량 조회 요청");
        try {
            UUID userId = securityUtil.getCurrentUserId();
            AIQuotaResponse quotaResponse = aiQuotaService.getUserQuota(userId);
            int remainingQuota = quotaResponse.getRemainingCount();
            log.info("GraphQL 쿼리 - AI 쿼터 잔여량 조회 완료: 잔여량={}", remainingQuota);
            return remainingQuota;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - AI 쿼터 잔여량 조회 실패", e);
            throw e;
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "AIQuotaQuery", field = "canUseAI")
    public boolean canUseAI(AIQuotaQuery source) {
        log.info("GraphQL 쿼리 - AI 사용 가능 여부 조회 요청");
        try {
            UUID userId = securityUtil.getCurrentUserId();
            AIQuotaResponse quotaResponse = aiQuotaService.getUserQuota(userId);
            boolean canUse = quotaResponse.getRemainingCount() > 0;
            log.info("GraphQL 쿼리 - AI 사용 가능 여부 조회 완료: 사용가능={}", canUse);
            return canUse;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - AI 사용 가능 여부 조회 실패", e);
            throw e;
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "AIQuotaQuery", field = "getMyQuota")
    public AIQuotaResponse getMyQuota(AIQuotaQuery source) {
        log.info("GraphQL 쿼리 - AI 쿼터 정보 조회 요청");
        try {
            UUID userId = securityUtil.getCurrentUserId();
            AIQuotaResponse response = aiQuotaService.getUserQuota(userId);

            response = AIQuotaResponse.builder()
                    .usageCount(response.getUsageCount())
                    .maxUsageCount(3) // AIQuotaService에 정의된 DAILY_QUOTA_LIMIT 값
                    .remainingCount(response.getRemainingCount())
                    .lastResetDate(response.getLastResetDate())
                    .canUseNow(response.getRemainingCount() > 0)
                    .build();

            log.info("GraphQL 쿼리 - AI 쿼터 정보 조회 완료: 사용량={}, 남은횟수={}, 사용가능={}",
                    response.getUsageCount(), response.getRemainingCount(), response.isCanUseNow());
            return response;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - AI 쿼터 정보 조회 실패", e);
            throw e;
        }
    }
}
