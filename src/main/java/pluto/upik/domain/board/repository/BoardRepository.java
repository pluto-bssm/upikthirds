package pluto.upik.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.board.data.model.Board;

import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
    Page<Board> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Board> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Board> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

    @Query(value = "SELECT b.* FROM boards b LEFT JOIN board_likes bl ON b.id = bl.board_id GROUP BY b.id ORDER BY COUNT(bl.id) DESC, b.created_at DESC", nativeQuery = true)
    Page<Board> findPopularBoards(Pageable pageable);

    // 댓글 수 기준으로 정렬 (인기순)
    @Query(value = "SELECT b.* FROM boards b LEFT JOIN comments c ON b.id = c.board_id GROUP BY b.id ORDER BY COUNT(c.id) DESC, b.created_at DESC",
           countQuery = "SELECT COUNT(DISTINCT b.id) FROM boards b",
           nativeQuery = true)
    Page<Board> findAllOrderByCommentCount(Pageable pageable);
}
