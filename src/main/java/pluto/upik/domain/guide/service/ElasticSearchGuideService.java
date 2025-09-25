package pluto.upik.domain.guide.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.data.model.GuideDocument;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchGuideService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final GuideRepository guideRepository;

    /**
     * 모든 가이드를 엘라스틱서치에 인덱싱합니다.
     * 이 메서드는 GraphQL 쿼리에서 직접 호출됩니다.
     */
    @Transactional(readOnly = true)
    public void indexAllGuides() {
        try {
            // 필요한 정보만 직접 조회하여 인덱싱
            List<Guide> guides = guideRepository.findAll();
            log.info("가이드 인덱싱 시작 - 총 가이드 수: {}", guides.size());

            if (guides.isEmpty()) {
                log.warn("인덱싱할 가이드가 없습니다.");
                return;
            }

            List<IndexQuery> indexQueries = new ArrayList<>();

            for (Guide guide : guides) {
                // 최소한의 정보만 포함하는 문서 생성
                GuideDocument document = GuideDocument.builder()
            .id(guide.getId())
                .title(guide.getTitle())
                .content(guide.getContent())
                .category(guide.getCategory())
            .guideType(guide.getGuideType())
            .like(guide.getLike())
                .revoteCount(guide.getRevoteCount())
            .createdAt(guide.getCreatedAt())
                .build();
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(document.getId().toString())
                .withObject(document)
                .build();
                indexQueries.add(indexQuery);
            }

            if (!indexQueries.isEmpty()) {
                elasticsearchOperations.bulkIndex(
                    indexQueries, IndexCoordinates.of("guides"));
        }

            log.info("전체 가이드 인덱싱 완료 - 가이드 수: {}", guides.size());
        } catch (Exception e) {
            log.error("전체 가이드 인덱싱 중 오류 발생 - error: {}", e.getMessage(), e);
            throw new BusinessException("전체 가이드 인덱싱 중 오류가 발생했습니다.");
}
    }

    /**
     * 제목 유사도 기반으로 가이드를 검색합니다.
     */
    public List<KeywordGuideResponse> searchSimilarGuidesByTitle(String title) {
        log.info("엘라스틱서치를 사용한 유사 제목 검색 시작 - title: {}", title);

        try {
            // 검색 쿼리 생성
            NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q -> q
                    .bool(b -> b
                        .should(s -> s
                            .match(m -> m
                                .field("title")
                                .query(title)
                                .boost(2.0f)
                            )
                        )
                        .should(s -> s
                            .match(m -> m
                                .field("content")
                                .query(title)
                            )
                        )
                        .minimumShouldMatch("1")
                    )
                )
                .withSort(Sort.by(Sort.Direction.DESC, "_score"))
                .withPageable(PageRequest.of(0, 10))
                .build();

            SearchHits<GuideDocument> searchHits = elasticsearchOperations.search(
                searchQuery, GuideDocument.class, IndexCoordinates.of("guides"));

            if (searchHits.isEmpty()) {
                log.warn("유사 제목 검색 결과 없음 - title: {}", title);

                // 더 관대한 검색 시도 (와일드카드)
                NativeQuery fallbackQuery = new NativeQueryBuilder()
                    .withQuery(q -> q
                        .bool(b -> b
                            .should(s -> s
                                .wildcard(w -> w
                                    .field("title")
                                    .value("*" + title + "*")
                                )
                            )
                            .should(s -> s
                                .wildcard(w -> w
                                    .field("content")
                                    .value("*" + title + "*")
                                )
                            )
                        )
                    )
                    .withSort(Sort.by(Sort.Direction.DESC, "_score"))
                    .withPageable(PageRequest.of(0, 10))
                    .build();

                searchHits = elasticsearchOperations.search(
                    fallbackQuery, GuideDocument.class, IndexCoordinates.of("guides"));

                if (searchHits.isEmpty()) {
                    throw new ResourceNotFoundException("유사한 제목의 가이드를 찾을 수 없습니다: " + title);
}
            }

            List<GuideDocument> documents = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

            // GuideDocument를 KeywordGuideResponse로 변환
            List<KeywordGuideResponse> responses = documents.stream()
                .map(doc -> convertToResponse(doc, title))
                .collect(Collectors.toList());

            log.info("유사 제목 검색 결과 - title: {}, 검색된 가이드 수: {}", title, responses.size());
            return responses;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("유사 제목 검색 중 오류 발생 - title: {}, error: {}", title, e.getMessage(), e);
            throw new BusinessException("유사 제목 검색 중 오류가 발생했습니다.");
        }
    }

    /**
     * GuideDocument를 KeywordGuideResponse로 변환합니다.
     */
    private KeywordGuideResponse convertToResponse(GuideDocument document, String keyword) {
        return KeywordGuideResponse.builder()
            .id(document.getId())
            .title(document.getTitle())
            .keyword(keyword)
            .content(document.getContent())
            .createdAt(document.getCreatedAt())
            .guideType(document.getGuideType())
            .category(document.getCategory())
            .likeCount(document.getLike())
            .revoteCount(document.getRevoteCount())
            .userId(document.getUserId())
            .userName(document.getUserName())
            .userEmail(document.getUserEmail())
            .build();
    }

    /**
     * 단일 가이드를 인덱싱합니다.
     */
    @Transactional(readOnly = true)
    public void indexGuide(UUID guideId) {
        try {
            Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("가이드를 찾을 수 없습니다: " + guideId));

            // 최소한의 정보만 포함하는 문서 생성
            GuideDocument document = GuideDocument.builder()
                .id(guide.getId())
                .title(guide.getTitle())
                .content(guide.getContent())
                .category(guide.getCategory())
                .guideType(guide.getGuideType())
                .like(guide.getLike())
                .revoteCount(guide.getRevoteCount())
                .createdAt(guide.getCreatedAt())
                .build();

            IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(document.getId().toString())
                .withObject(document)
                .build();

            elasticsearchOperations.index(indexQuery, IndexCoordinates.of("guides"));
            log.info("가이드 인덱싱 완료 - ID: {}, 제목: {}", guide.getId(), guide.getTitle());
        } catch (Exception e) {
            log.error("가이드 인덱싱 중 오류 발생 - ID: {}, error: {}", guideId, e.getMessage(), e);
            throw new BusinessException("가이드 인덱싱 중 오류가 발생했습니다.");
        }
    }

    /**
     * 가이드를 인덱스에서 삭제합니다.
     */
    public void deleteGuideFromIndex(UUID guideId) {
        try {
            elasticsearchOperations.delete(guideId.toString(), IndexCoordinates.of("guides"));
            log.info("가이드 인덱스 삭제 완료 - ID: {}", guideId);
        } catch (Exception e) {
            log.error("가이드 인덱스 삭제 중 오류 발생 - ID: {}, error: {}", guideId, e.getMessage(), e);
            throw new BusinessException("가이드 인덱스 삭제 중 오류가 발생했습니다.");
        }
    }
}
