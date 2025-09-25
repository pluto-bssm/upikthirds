package pluto.upik.domain.revote.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.revote.data.DTO.RevoteMutation;
import pluto.upik.domain.revote.data.DTO.RevoteRequestInput;
import pluto.upik.domain.revote.data.DTO.RevoteRequestResponse;
import pluto.upik.domain.revote.service.RevoteServiceInterface;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

/**
 * 재투표 GraphQL Mutation 리졸버
 * 
 * 재투표 요청과 관련된 GraphQL 뮤테이션을 처리합니다.
 * 
 * @author upik-team
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RevoteMutationResolver {

    private final RevoteServiceInterface revoteService;
    private final SecurityUtil securityUtil;

    /**
     * 재투표 요청을 생성합니다.
     * 
     * @param parent GraphQL parent object (미사용)
     * @param input 재투표 요청 입력 데이터
     * @return RevoteRequestResponse 생성된 재투표 요청 정보
     * @throws BusinessException 비즈니스 로직 위반 시
     */
    @RequireAuth
    @SchemaMapping(typeName = "RevoteMutation", field = "createRevote")
    public RevoteRequestResponse createRevote(RevoteMutation parent, @Argument RevoteRequestInput input) {
        final UUID guideId = input.getGuideId();
        final String reason = input.getReason();
        
        log.info("재투표 요청 생성 시작 - guideId: {}, reason: {}", guideId, reason);
        
        try {
            // 현재 인증된 사용자 정보 조회
            final UUID currentUserId = securityUtil.getCurrentUserId();
            log.debug("현재 사용자 ID: {}", currentUserId);
            
            // 입력 데이터 검증
            validateRevoteInput(input);
            
            // 재투표 요청 생성
            final RevoteRequestResponse response = revoteService.createRevoteRequest(currentUserId, input);
            
            log.info("재투표 요청 생성 완료 - requestId: {}, guideId: {}, userId: {}", 
                    response.getId(), guideId, currentUserId);
            
            return response;
            
        } catch (BusinessException e) {
            log.warn("재투표 요청 생성 실패 (비즈니스 로직 위반) - guideId: {}, reason: {}, error: {}", 
                    guideId, reason, e.getMessage());
            throw e;
            
        } catch (Exception e) {
            log.error("재투표 요청 생성 중 예상치 못한 오류 발생 - guideId: {}, reason: {}", 
                    guideId, reason, e);
            throw new BusinessException("재투표 요청 처리 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 재투표 요청 입력 데이터의 유효성을 검증합니다.
     * 
     * @param input 검증할 입력 데이터
     * @throws BusinessException 유효하지 않은 입력 데이터인 경우
     */
    private void validateRevoteInput(RevoteRequestInput input) {
        if (input.getGuideId() == null) {
            throw new BusinessException("가이드 ID는 필수 입력값입니다.");
        }
        
        if (input.getReason() == null || input.getReason().trim().isEmpty()) {
            throw new BusinessException("재투표 요청 이유는 필수 입력값입니다.");
        }
        
        if (input.getReason().length() > 255) {
            throw new BusinessException("재투표 요청 이유는 255자를 초과할 수 없습니다.");
        }
        
        if (input.getDetailReason() != null && input.getDetailReason().length() > 1000) {
            throw new BusinessException("상세 이유는 1000자를 초과할 수 없습니다.");
        }
    }
}