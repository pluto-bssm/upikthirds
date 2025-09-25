package pluto.upik.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.board.data.model.Comment;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByBoardIdAndParentIdIsNull(UUID boardId, Pageable pageable);
    List<Comment> findByParentId(UUID parentId);
    long countByBoardId(UUID boardId);
    void deleteByBoardId(UUID boardId);
}