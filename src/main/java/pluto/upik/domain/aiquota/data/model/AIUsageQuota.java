package pluto.upik.domain.aiquota.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AIUsageQuota {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    private int usageCount; // 당일 사용 횟수

    private LocalDate lastResetDate; // 마지막 초기화 날짜

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.lastResetDate = LocalDate.now();
        this.usageCount = 0;
    }
}