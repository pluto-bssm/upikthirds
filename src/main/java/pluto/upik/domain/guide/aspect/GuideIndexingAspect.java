package pluto.upik.domain.guide.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.service.ElasticSearchGuideService;

import java.util.UUID;

/**
 * 가이드 데이터 변경 시 엘라스틱서치 인덱스를 자동으로 업데이트하는 AOP 컴포넌트
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class GuideIndexingAspect {

    private final ElasticSearchGuideService elasticSearchGuideService;

    /**
     * 가이드 저장 후 엘라스틱서치 인덱스 업데이트
     */
    @AfterReturning(
        pointcut = "execution(* pluto.upik.domain.guide.repository.GuideRepository.save(..)) || " +
                  "execution(* pluto.upik.shared.ai.service.AIService.generateAndSaveGuide(..))",
        returning = "result")
    public void afterGuideSave(JoinPoint joinPoint, Object result) {
        try {
            Guide guide = null;
            
            if (result instanceof Guide) {
                guide = (Guide) result;
            }
            
            // AIService.generateAndSaveGuide() 메서드의 경우 GuideResponseDTO를 반환하므로 별도 처리
            if (result instanceof pluto.upik.shared.ai.data.DTO.GuideResponseDTO) {
                pluto.upik.shared.ai.data.DTO.GuideResponseDTO dto = 
                    (pluto.upik.shared.ai.data.DTO.GuideResponseDTO) result;
                UUID guideId = dto.getId();
                if (guideId != null) {
                    elasticSearchGuideService.indexGuide(guideId);
                    log.info("가이드 저장 후 엘라스틱서치 인덱스 업데이트 완료 - guideId: {}", guideId);
                }
                return;
            }
            
            if (guide != null && guide.getId() != null) {
                elasticSearchGuideService.indexGuide(guide.getId());
                log.info("가이드 저장 후 엘라스틱서치 인덱스 업데이트 완료 - guideId: {}", guide.getId());
            }
        } catch (Exception e) {
            log.error("가이드 저장 후 엘라스틱서치 인덱스 업데이트 중 오류 발생", e);
        }
    }

    /**
     * 가이드 삭제 후 엘라스틱서치 인덱스에서도 삭제
     */
    @AfterReturning(
        pointcut = "execution(* pluto.upik.domain.guide.repository.GuideRepository.delete(..)) || " +
                  "execution(* pluto.upik.domain.guide.repository.GuideRepository.deleteById(..))")
    public void afterGuideDelete(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                if (args[0] instanceof Guide) {
                    Guide guide = (Guide) args[0];
                    elasticSearchGuideService.deleteGuideFromIndex(guide.getId());
                    log.info("가이드 삭제 후 엘라스틱서치 인덱스에서 삭제 완료 - guideId: {}", guide.getId());
                } else if (args[0] instanceof UUID) {
                    UUID guideId = (UUID) args[0];
                    elasticSearchGuideService.deleteGuideFromIndex(guideId);
                    log.info("가이드 삭제 후 엘라스틱서치 인덱스에서 삭제 완료 - guideId: {}", guideId);
                }
            }
        } catch (Exception e) {
            log.error("가이드 삭제 후 엘라스틱서치 인덱스 업데이트 중 오류 발생", e);
        }
    }
    
    /**
     * 가이드 일괄 변경 후 엘라스틱서치 인덱스 전체 재구축
     */
    @AfterReturning(
        pointcut = "execution(* pluto.upik.domain.guide.repository.GuideRepository.saveAll(..))")
    public void afterGuideBulkOperation(JoinPoint joinPoint) {
        try {
            log.info("가이드 일괄 변경 감지, 엘라스틱서치 인덱스 전체 재구축 시작");
            elasticSearchGuideService.indexAllGuides();
            log.info("엘라스틱서치 인덱스 전체 재구축 완료");
        } catch (Exception e) {
            log.error("가이드 일괄 변경 후 엘라스틱서치 인덱스 재구축 중 오류 발생", e);
        }
    }
}