package pluto.upik.domain.guide.service;

import pluto.upik.domain.guide.data.model.Guide;

import java.util.List;
import java.util.UUID;

/**
 * 가이드 상호작용(좋아요, 신고 등) 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface GuideInteractionServiceInterface {

    /**
     * 특정 사용자가 특정 가이드에 좋아요를 토글합니다.
     * 이미 좋아요 되어있으면 좋아요 취소(삭제) 후 카운트 감소
     * 좋아요 없으면 저장 후 카운트 증가
     *
     * @param userId 사용자 ID
     * @param guideId 가이드 ID
     * @return 좋아요가 추가되었으면 true, 취소되었으면 false
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 사용자나 가이드가 존재하지 않을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 처리 중 오류 발생 시
     */
    boolean toggleLikeGuide(UUID userId, UUID guideId);

    /**
 * 사용자가 특정 가이드에 대해 재투표 신고를 추가하거나 취소합니다.
 *
 * 이미 신고한 경우 신고를 취소하고 재투표 카운트를 감소시키며, 신고하지 않은 경우 신고를 추가하고 재투표 카운트를 증가시킵니다.
 *
 * @param guideId 신고 대상 가이드의 UUID
 * @param userId 신고를 수행하는 사용자의 UUID
 * @param reason 신고 사유
 * @return 신고가 추가되면 true, 취소되면 false
 * @throws pluto.upik.shared.exception.ResourceNotFoundException 사용자가 존재하지 않거나 가이드를 찾을 수 없는 경우
 * @throws pluto.upik.shared.exception.BusinessException 처리 중 비즈니스 로직 오류가 발생한 경우
 */
    boolean toggleReportAndRevote(UUID guideId, UUID userId, String reason);
    
    /**
 * 지정한 사용자가 생성한 모든 가이드 목록을 반환합니다.
 *
 * @param userId 가이드를 생성한 사용자의 UUID
 * @return 해당 사용자가 생성한 가이드 객체의 리스트
 * @throws pluto.upik.shared.exception.ResourceNotFoundException 사용자가 존재하지 않을 경우
 * @throws pluto.upik.shared.exception.BusinessException 처리 중 오류가 발생한 경우
 */
    List<Guide> getUserCreatedGuides(UUID userId);
}