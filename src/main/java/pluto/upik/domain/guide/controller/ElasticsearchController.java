package pluto.upik.domain.guide.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pluto.upik.domain.guide.service.ElasticSearchGuideService;

@Slf4j
@RestController
@RequestMapping("/api/elasticsearch")
@RequiredArgsConstructor
public class ElasticsearchController {

    private final ElasticSearchGuideService elasticSearchGuideService;

    @PostMapping("/index-all-guides")
    public ResponseEntity<String> indexAllGuides() {
        try {
            log.info("모든 가이드 인덱싱 API 호출");
            elasticSearchGuideService.indexAllGuides();
            return ResponseEntity.ok("모든 가이드가 성공적으로 인덱싱되었습니다.");
        } catch (Exception e) {
            log.error("가이드 인덱싱 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("가이드 인덱싱 중 오류 발생: " + e.getMessage());
        }
    }
}