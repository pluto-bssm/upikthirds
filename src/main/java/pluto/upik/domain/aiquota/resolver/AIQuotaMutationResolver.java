package pluto.upik.domain.aiquota.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.aiquota.data.DTO.AIQuotaMutation;
import pluto.upik.domain.aiquota.data.DTO.AIQuotaResponse;
import pluto.upik.domain.aiquota.service.AIQuotaServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AIQuotaMutationResolver {

    private final AIQuotaServiceInterface aiQuotaService;
    private final SecurityUtil securityUtil;

    @RequireAuth
    @SchemaMapping(typeName = "AIQuotaMutation", field = "useAIQuota")
    public AIQuotaResponse useAIQuota(AIQuotaMutation parent) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            return aiQuotaService.useAIQuota(userId);
        } catch (Exception e) {
            log.error("AI 쿼터 사용 중 오류 발생", e);
            throw new RuntimeException("AI 쿼터 사용 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "AIQuotaMutation", field = "resetAIQuota")
    public AIQuotaResponse resetAIQuota(AIQuotaMutation parent, @Argument String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            return aiQuotaService.resetAIQuota(userUuid);
        } catch (Exception e) {
            log.error("AI 쿼터 초기화 중 오류 발생: userId={}", userId, e);
            throw new RuntimeException("AI 쿼터 초기화 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "AIQuotaMutation", field = "increaseAIQuota")
    public AIQuotaResponse increaseAIQuota(AIQuotaMutation parent, @Argument int amount) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            return aiQuotaService.increaseAIQuota(userId, amount);
        } catch (Exception e) {
            log.error("AI 쿼터 증가 중 오류 발생: amount={}", amount, e);
            throw new RuntimeException("AI 쿼터 증가 중 오류가 발생했습니다.", e);
        }
    }
}
