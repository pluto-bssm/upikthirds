package pluto.upik.domain.board.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment_likes")
@Getter
@Setter
@NoArgsConstructor
public class CommentLike {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "comment_id", nullable = false, columnDefinition = "uuid")
    private UUID commentId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}