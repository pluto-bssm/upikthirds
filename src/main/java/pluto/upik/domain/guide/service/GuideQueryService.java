package pluto.upik.domain.guide.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuidePage;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 가이드 조회 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuideQueryService implements GuideQueryServiceInterface {

    private final GuideRepository guideRepository;

    /**
     * 지정한 카테고리에 해당하는 모든 가이드 목록을 조회하여 GuideResponse 리스트로 반환합니다.
     *
     * 카테고리에 해당하는 가이드가 없을 경우 빈 리스트를 반환합니다.
     * 각 GuideResponse에는 가이드의 ID, 제목, 내용, 생성일, 좋아요 수, 투표 ID가 포함됩니다.
     *
     * @param category 조회할 가이드의 카테고리
     * @return 해당 카테고리의 가이드 정보를 담은 GuideResponse 리스트
     */
    @Override
    public List<GuideResponse> findByCategory(String category) {
        log.info("findByCategory called with category: {}", category);

        try {
            List<Guide> guides = guideRepository.findAllByCategory(category);
            log.info("카테고리별 가이드 조회 결과 - category: {}, 조회된 가이드 수: {}", category, guides.size());

            if (guides == null || guides.isEmpty()) {
                log.info("카테고리별 가이드 조회 결과 없음 - category: {}, 빈 리스트 반환", category);
                return List.of(); // 빈 배열 반환
            }

            List<GuideResponse> responses = guides.stream()
                    .map(guide -> GuideResponse.builder()
                            .id(guide.getId())
                            .title(guide.getTitle())
                            .content(guide.getContent())
                            .createdAt(guide.getCreatedAt())
                            .like(guide.getLike() != null ? guide.getLike().intValue() : 0) // likeCount → like로 변경 및 null 체크 추가
                            .voteId(guide.getVote() != null ? guide.getVote().getId() : null) // 투표 ID 추가
                            .build())
                    .collect(Collectors.toList());

            log.info("Number of guides found: {}", responses.size());
            return responses;
        } catch (Exception e) {
            log.error("가이드 조회 중 예외 발생 - category: {}, error: {}", category, e.getMessage(), e);
            throw new BusinessException("가이드 조회 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GuideDetailResponse findGuideById(UUID guideId) {
        log.info("가이드 상세 조회 요청 시작 - guideId: {}", guideId);
        
        try {
            Guide guide = guideRepository.findById(guideId)
                    .orElseThrow(() -> {
                        log.warn("가이드 상세 조회 실패 - 가이드 없음 (guideId: {})", guideId);
                        return new ResourceNotFoundException("해당 ID의 가이드를 찾을 수 없습니다: " + guideId);
                    });
            
            GuideDetailResponse response = GuideDetailResponse.builder()
                    .id(guide.getId())
                    .title(guide.getTitle())
                    .content(guide.getContent())
                    .createdAt(guide.getCreatedAt())
                    .category(guide.getCategory())
                    .guideType(guide.getGuideType())
                    .likeCount(guide.getLike() != null ? guide.getLike().intValue() : 0) // null 체크 추가
                    .revoteCount(guide.getRevoteCount() != null ? guide.getRevoteCount().intValue() : 0) // null 체크 추가
                    .voteId(guide.getVote() != null ? guide.getVote().getId() : null)
                    .build();
            
            log.info("가이드 상세 조회 완료 - guideId: {}", guideId);
            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("가이드 상세 조회 중 예외 발생 - guideId: {}, error: {}", guideId, e.getMessage(), e);
            throw new BusinessException("가이드 상세 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 모든 가이드를 페이징하여 조회합니다.
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 기준 ("date" 또는 "bookmark")
     * @return 페이징된 가이드 정보
     */
    @Override
    public GuidePage getAllGuides(int page, int size, String sortBy) {
        log.info("모든 가이드 조회 요청 - page: {}, size: {}, sortBy: {}", page, size, sortBy);

        try {
        Pageable pageable = PageRequest.of(page, size);
        Page<Guide> guidesPage;

        if ("bookmark".equalsIgnoreCase(sortBy)) {
            // 북마크 수 기준 정렬
            guidesPage = guideRepository.findAllOrderByBookmarkCount(pageable);
        } else {
            // 기본: 생성일 기준 정렬 (최신순)
            guidesPage = guideRepository.findAllByOrderByCreatedAtDesc(pageable);
}

            List<GuideResponse> content = guidesPage.getContent().stream()
                .map(this::mapToGuideResponse)
                .collect(Collectors.toList());

            GuidePage result = GuidePage.builder()
                    .content(content)
                    .totalElements(guidesPage.getTotalElements())
                    .totalPages(guidesPage.getTotalPages())
                    .pageNumber(guidesPage.getNumber())
                    .size(guidesPage.getSize())
                    .hasNext(guidesPage.hasNext())
                .build();

            log.info("모든 가이드 조회 완료 - 총 가이드 수: {}, 총 페이지 수: {}",
                    result.getTotalElements(), result.getTotalPages());
            return result;
        } catch (Exception e) {
            log.error("가이드 목록 조회 중 예외 발생 - error: {}", e.getMessage(), e);
            throw new BusinessException("가이드 목록 조회 중 오류가 발생했습니다.");
}
}

    /**
     * Guide 엔티티를 GuideResponse DTO로 변환합니다.
     *
     * @param guide 변환할 Guide 엔티티
     * @return 변환된 GuideResponse 객체
     */
    private GuideResponse mapToGuideResponse(Guide guide) {
        return GuideResponse.builder()
                .id(guide.getId())
                .title(guide.getTitle())
                .category(guide.getCategory())
                .content(guide.getContent())
                .createdAt(guide.getCreatedAt())
                .like(guide.getLike() != null ? guide.getLike().intValue() : 0)
                .voteId(guide.getVote() != null ? guide.getVote().getId() : null)
                .build();
    }
}
