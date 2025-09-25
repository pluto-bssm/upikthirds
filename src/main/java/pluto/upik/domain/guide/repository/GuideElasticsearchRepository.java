package pluto.upik.domain.guide.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pluto.upik.domain.guide.data.model.GuideDocument;

import java.util.UUID;

public interface GuideElasticsearchRepository extends ElasticsearchRepository<GuideDocument, UUID> {
    // 기본 메서드만 사용하고 복잡한 쿼리는 ElasticSearchGuideService에서 직접 구현
}