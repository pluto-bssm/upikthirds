package pluto.upik.domain.vote.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.vote.data.model.Vote;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 투표 레포지토리
 * 투표 엔티티에 대한 데이터베이스 접근을 제공합니다.
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    /**
     * 종료 날짜가 지났고 상태가 OPEN인 투표 목록을 조회합니다.
     *
     * @param currentDate 현재 날짜
     * @return 가이드 생성이 필요한 투표 목록
     */
    @Query("SELECT v FROM Vote v WHERE v.finishedAt <= :currentDate AND v.status = pluto.upik.domain.vote.data.model.Vote.Status.OPEN")
    List<Vote> findFinishedVotesWithoutGuide(LocalDate currentDate);

    /**
     * 특정 사용자가 생성한 투표 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 생성한 투표 목록
     */
    List<Vote> findByUserId(UUID userId);

    /**
     * 종료 날짜가 지나지 않은 투표 목록을 조회합니다.
     *
     * @param currentDate 현재 날짜
     * @return 진행 중인 투표 목록
     */
    @Query("SELECT v FROM Vote v WHERE v.finishedAt > :currentDate")
    List<Vote> findActiveVotes(LocalDate currentDate);

    /**
     * 종료 날짜가 지난 투표 목록을 조회합니다.
     *
     * @param date 기준 날짜
     * @return 종료된 투표 목록
     */
    List<Vote> findByFinishedAtBefore(LocalDate date);

    /**
     * 특정 상태의 투표 목록을 조회합니다.
     *
     * @param status 투표 상태
     * @return 해당 상태의 투표 목록
     */
    List<Vote> findByStatus(Vote.Status status);
    
    /**
     * 특정 상태이면서 종료 날짜가 지난 투표 목록을 조회합니다.
     *
     * @param status 투표 상태
     * @param date 기준 날짜
     * @return 종료된 투표 목록
     */
    List<Vote> findByStatusAndFinishedAtBefore(Vote.Status status, LocalDate date);
    
    /**
     * 생성일 기준으로 정렬된 투표 목록을 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지 정보
     * @return 정렬된 투표 페이지
     */
    @Query("SELECT v FROM Vote v ORDER BY v.finishedAt DESC")
    Page<Vote> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 참여율 기준으로 정렬된 투표 목록을 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지 정보
     * @return 정렬된 투표 페이지
     */
    @Query(value = "SELECT v.* FROM vote v " +
           "LEFT JOIN (SELECT vote_id, COUNT(*) as response_count FROM vote_response GROUP BY vote_id) vr " +
           "ON v.id = vr.vote_id " +
           "ORDER BY vr.response_count DESC NULLS LAST", 
           nativeQuery = true)
    Page<Vote> findAllOrderByParticipationRate(Pageable pageable);
    
    /**
     * 종료율 기준으로 정렬된 투표 목록을 페이지네이션하여 조회합니다.
     * 종료율 = 현재 참여자 수 / 종료 기준 참여자 수
     *
     * @param pageable 페이지 정보
     * @return 정렬된 투표 페이지
     */
    @Query(value = "SELECT v.* FROM vote v " +
           "LEFT JOIN (SELECT vote_id, COUNT(*) as response_count FROM vote_response GROUP BY vote_id) vr " +
           "ON v.id = vr.vote_id " +
           "WHERE v.participant_threshold IS NOT NULL AND v.status = 'OPEN' " +
           "ORDER BY (vr.response_count / v.participant_threshold) DESC NULLS LAST", 
           nativeQuery = true)
    Page<Vote> findAllOrderByCompletionRate(Pageable pageable);
}