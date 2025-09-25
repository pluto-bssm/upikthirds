package pluto.upik.domain.guide.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pluto.upik.domain.guide.service.ElasticSearchGuideService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final ElasticSearchGuideService elasticSearchGuideService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeElasticsearchIndices() {
        try {
            log.info("애플리케이션 시작 후 엘라스틱서치 인덱스 초기화 시작");
            elasticSearchGuideService.indexAllGuides();
            log.info("엘라스틱서치 인덱스 초기화 완료");
        } catch (Exception e) {
            log.error("엘라스틱서치 인덱스 초기화 중 오류 발생", e);
        }
    }
}