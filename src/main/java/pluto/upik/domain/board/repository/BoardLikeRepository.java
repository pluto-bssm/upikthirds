package pluto.upik.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.board.data.model.BoardLike;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, UUID> {
    Optional<BoardLike> findByUserIdAndBoardId(UUID userId, UUID boardId);

    int countByBoardId(UUID boardId);

    @Query("SELECT bl.boardId FROM BoardLike bl WHERE bl.userId = :userId")
    Page<UUID> findBoardIdsByUserId(UUID userId, Pageable pageable);

    void deleteByUserIdAndBoardId(UUID userId, UUID boardId);
}