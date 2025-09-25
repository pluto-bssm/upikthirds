package pluto.upik.domain.revote.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재투표 요청 엔티티
 * 
 * 사용자가 특정 가이드에 대해 재투표를 요청한 정보를 저장하는 엔티티입니다.
 * 한 사용자는 같은 가이드에 대해 한 번만 재투표를 요청할 수 있습니다.
 * 
 * @author upik-team
 * @version 2.0
 * @since 2024
 */
@Entity
@Table(
    name = "revote_request",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_revote_user_guide",
            columnNames = {"user_id", "guide_id"}
        )
    },
    indexes = {
        @Index(name = "idx_revote_guide_id", columnList = "guide_id"),
        @Index(name = "idx_revote_user_id", columnList = "user_id"),
        @Index(name = "idx_revote_created_at", columnList = "created_at")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RevoteRequest {

    /**
     * 재투표 요청 ID (기본 키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 재투표를 요청한 사용자 ID
     */
    @NotNull
    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    /**
     * 재투표가 요청된 가이드 ID
     */
    @NotNull
    @Column(name = "guide_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID guideId;

    /**
     * 재투표 요청 이유 (주요 카테고리)
     * 필수 입력값이며, 최대 255자까지 입력 가능합니다.
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    /**
     * 재투표 요청 상세 이유
     * 선택사항이며, 최대 1000자까지 입력 가능합니다.
     */
    @Size(max = 1000)
    @Column(name = "detail_reason", length = 1000)
    private String detailReason;

    /**
     * 재투표 요청 상태
     * PENDING: 대기중, APPROVED: 승인됨, REJECTED: 거부됨
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private RevoteRequestStatus status = RevoteRequestStatus.PENDING;

    /**
     * 재투표 요청 생성 일시
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 재투표 요청 수정 일시
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 재투표 요청 상태 변경
     * 
     * @param status 변경할 상태
     */
    public void updateStatus(RevoteRequestStatus status) {
        this.status = status;
    }

    /**
     * 재투표 요청이 승인 가능한 상태인지 확인
     * 
     * @return boolean 승인 가능 여부
     */
    public boolean canBeApproved() {
        return this.status == RevoteRequestStatus.PENDING;
    }

    /**
     * 재투표 요청이 거부 가능한 상태인지 확인
     * 
     * @return boolean 거부 가능 여부
     */
    public boolean canBeRejected() {
        return this.status == RevoteRequestStatus.PENDING;
    }

    /**
     * 재투표 요청 상태 열거형
     */
    public enum RevoteRequestStatus {
        /**
         * 대기중 - 재투표 요청이 생성되었지만 아직 처리되지 않은 상태
         */
        PENDING("대기중"),
        
        /**
         * 승인됨 - 재투표 요청이 승인되어 실제 재투표가 진행된 상태
         */
        APPROVED("승인됨"),
        
        /**
         * 거부됨 - 재투표 요청이 거부된 상태
         */
        REJECTED("거부됨");

        private final String description;

        RevoteRequestStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}