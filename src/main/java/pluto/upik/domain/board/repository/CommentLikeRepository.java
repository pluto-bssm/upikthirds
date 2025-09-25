package pluto.upik.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.board.data.model.CommentLike;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {
    Optional<CommentLike> findByUserIdAndCommentId(UUID userId, UUID commentId);

    int countByCommentId(UUID commentId);

    void deleteByUserIdAndCommentId(UUID userId, UUID commentId);
}