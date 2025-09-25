package pluto.upik.domain.guide.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pluto.upik.domain.guide.service.ElasticSearchGuideService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ElasticsearchInitializer {

    private final ElasticSearchGuideService elasticSearchGuideService;

    /**
     * 애플리케이션 시작 시 모든 가이드 데이터를 엘라스틱서치에 인덱싱합니다.
     * 개발 및 운영 환경에서만 실행됩니다.
     */
    @Bean
    @Profile({"dev", "prod"})
    public CommandLineRunner initializeElasticsearchIndices() {
        return args -> {
            log.info("엘라스틱서치 인덱스 초기화 시작");
            try {
                elasticSearchGuideService.indexAllGuides();
                log.info("엘라스틱서치 인덱스 초기화 완료");
            } catch (Exception e) {
                log.error("엘라스틱서치 인덱스 초기화 중 오류 발생", e);
            }
        };
    }
}