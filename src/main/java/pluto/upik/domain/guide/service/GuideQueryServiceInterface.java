package pluto.upik.domain.guide.service;

import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuidePage;
import pluto.upik.domain.guide.data.DTO.GuideResponse;

import java.util.List;
import java.util.UUID;

/**
 * 가이드 조회 관련 비즈니스 로직을 정의하는 인터페이스
 */
public interface GuideQueryServiceInterface {

    /**
     * 지정한 카테고리에 해당하는 모든 가이드 목록을 조회합니다.
     *
     * @param category 조회할 가이드의 카테고리
     * @return 해당 카테고리의 가이드 정보를 담은 GuideResponse 리스트
     */
    List<GuideResponse> findByCategory(String category);
    
    /**
     * 특정 ID를 가진 가이드의 상세 정보를 조회합니다.
     *
     * @param guideId 조회할 가이드의 ID
     * @return 가이드 상세 정보를 담은 GuideDetailResponse
     */
    GuideDetailResponse findGuideById(UUID guideId);

    /**
     * 모든 가이드를 페이징하여 조회합니다.
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 기준 ("date" 또는 "bookmark")
     * @return 페이징된 가이드 정보
     */
    GuidePage getAllGuides(int page, int size, String sortBy);
}