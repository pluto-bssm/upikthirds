package pluto.upik.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.bookmark.data.model.BoardBookmark;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, UUID> {
    List<BoardBookmark> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<BoardBookmark> findByUserIdAndBoardId(UUID userId, UUID boardId);
    boolean existsByUserIdAndBoardId(UUID userId, UUID boardId);
    long countByBoardId(UUID boardId);

    @Query("SELECT b.boardId FROM BoardBookmark b GROUP BY b.boardId ORDER BY COUNT(b.id) DESC")
    List<UUID> findBoardIdOrderByBookmarkCountDesc();
}
