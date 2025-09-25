package pluto.upik.domain.notification.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    private String type; // VOTE_ENDED, GUIDE_CREATED, QUESTION_REPLY

    private String title;

    private String content;

    @Column(columnDefinition = "BINARY(16)")
    private UUID referenceId; // 참조하는 엔티티의 ID (투표, 가이드, 질문 등)

    private boolean read;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.read = false;
    }
}