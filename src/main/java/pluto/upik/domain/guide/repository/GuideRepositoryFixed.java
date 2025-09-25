package pluto.upik.domain.guide.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.guide.data.model.Guide;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 가이드 레포지토리
 * 가이드 엔티티에 대한 데이터베이스 접근을 제공합니다.
 */
@Repository
public interface GuideRepositoryFixed extends JpaRepository<Guide, UUID> {

    /**
 * 주어진 투표 ID에 연결된 가이드 엔티티를 조회합니다.
 *
 * @param voteId 조회할 투표의 UUID
 * @return 해당 투표에 연결된 가이드가 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    Optional<Guide> findByVoteId(UUID voteId);

    /**
 * 주어진 투표 ID에 해당하는 가이드가 데이터베이스에 존재하는지 여부를 반환합니다.
 *
 * @param voteId 확인할 투표의 UUID
 * @return 가이드가 존재하면 true, 없으면 false
 */
    boolean existsByVoteId(UUID voteId);

    /**
 * 지정한 카테고리에 속한 모든 가이드 엔티티 목록을 반환합니다.
 *
 * @param category 조회할 가이드의 카테고리 이름
 * @return 해당 카테고리에 속한 가이드 목록
 */
    List<Guide> findAllByCategory(String category);

    /**
     * 제목에 주어진 키워드가 포함된 모든 가이드를 조회합니다.
     *
     * @param keyword 가이드 제목에서 검색할 키워드
     * @return 키워드가 제목에 포함된 가이드의 리스트
     */
    @Query("SELECT g FROM Guide g WHERE g.title LIKE %:keyword%")
    List<Guide> findGuidesByTitleContaining(@Param("keyword") String keyword);

    /**
     * 주어진 사용자 ID로 해당 사용자가 생성한 모든 가이드 목록을 조회합니다.
     *
     * @param userId 가이드를 생성한 사용자의 UUID
     * @return 사용자가 생성한 가이드의 리스트
     */
    @Query("SELECT g FROM Guide g JOIN g.vote v WHERE v.user.id = :userId")
    List<Guide> findGuidesByUserId(@Param("userId") UUID userId);

    /**
     * 지정된 가이드의 좋아요 수를 1 증가시킵니다.
     *
     * @param guideId 좋아요 수를 증가시킬 가이드의 UUID
     * @return 업데이트된 행의 수
     */
    @Transactional
    @Modifying
    @Query("UPDATE Guide g SET g.like = g.like + 1 WHERE g.id = :guideId")
    int incrementLikeCount(@Param("guideId") UUID guideId);

    /**
     * 지정한 가이드의 재투표 수를 1 증가시킵니다.
     *
     * @param id 재투표 수를 증가시킬 가이드의 UUID
     * @return 변경된 행의 수
     */
    @Transactional
    @Modifying
    @Query("UPDATE Guide g SET g.revoteCount = g.revoteCount + 1 WHERE g.id = :id")
    int incrementRevoteCount(@Param("id") UUID id);

    /**
     * 지정한 가이드의 좋아요 수를 1 감소시킵니다. 좋아요 수가 0 이하로 내려가지 않도록 제한합니다.
     *
     * @param id 감소시킬 가이드의 UUID
     * @return 좋아요 수가 감소된 경우 1, 그렇지 않으면 0을 반환합니다.
     */
    @Modifying
    @Transactional
    @Query("update Guide g set g.like = g.like - 1 where g.id = :id and g.like > 0")
    int decrementLikeCount(@Param("id") UUID id);

    /**
     * 지정한 가이드의 재투표 수를 1 감소시킵니다. 재투표 수가 0 이하로 내려가지 않도록 제한합니다.
     *
     * @param id 감소시킬 가이드의 UUID
     * @return 감소가 적용된 행의 수
     */
    @Modifying
    @Transactional
    @Query("update Guide g set g.revoteCount = g.revoteCount - 1 where g.id = :id and g.revoteCount > 0")
    int decrementRevoteCount(@Param("id") UUID id);
}