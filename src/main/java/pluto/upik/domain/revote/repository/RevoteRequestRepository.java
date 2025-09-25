package pluto.upik.domain.revote.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.revote.data.model.RevoteRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 재투표 요청 Repository 인터페이스
 */
@Repository
public interface RevoteRequestRepository extends JpaRepository<RevoteRequest, Long> {

    /**
     * 특정 가이드에 대한 재투표 요청 목록을 생성일시 내림차순으로 조회
     */
    List<RevoteRequest> findByGuideIdOrderByCreatedAtDesc(UUID guideId);

    /**
     * 특정 가이드에 대한 재투표 요청 목록을 페이징하여 조회
     */
    Page<RevoteRequest> findByGuideIdOrderByCreatedAtDesc(UUID guideId, Pageable pageable);

    /**
     * 특정 사용자의 재투표 요청 목록을 생성일시 내림차순으로 조회
     */
    List<RevoteRequest> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * 사용자가 특정 가이드에 대해 재투표 요청했는지 확인
     */
    boolean existsByUserIdAndGuideId(UUID userId, UUID guideId);

    /**
     * 특정 사용자와 가이드에 대한 재투표 요청 조회
     */
    Optional<RevoteRequest> findByUserIdAndGuideId(UUID userId, UUID guideId);

    /**
     * 상태별 재투표 요청 개수 조회
     */
    long countByStatus(RevoteRequest.RevoteRequestStatus status);

    /**
     * 특정 가이드에 대한 재투표 요청 개수 조회
     */
    long countByGuideId(UUID guideId);

    /**
     * 특정 가이드와 상태에 따른 재투표 요청 개수 조회
     */
    long countByGuideIdAndStatus(UUID guideId, RevoteRequest.RevoteRequestStatus status);

    /**
     * 시간 범위 내 재투표 요청 목록 조회
     */
    List<RevoteRequest> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 인기 재투표 대상 가이드 조회 (가장 많이 재투표 요청받은 가이드들)
     */
    @Query("SELECT r.guideId, COUNT(r) as requestCount " +
           "FROM RevoteRequest r " +
           "GROUP BY r.guideId " +
           "ORDER BY requestCount DESC")
    List<Object[]> findPopularRevoteTargets(Pageable pageable);
}