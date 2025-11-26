package pluto.upik.domain.report.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 신고 엔티티
 * 사용자가 제출한 신고 정보를 저장합니다.
 */
@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Report {
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Id
    private UUID id;

    /**
     * 신고자 ID (복합 키)
     */
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    /**
     * 신고 대상 ID (복합 키)
     */
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID targetId;

    /**
     * 신고 사유
     */
    @Column(columnDefinition = "TEXT")
    private String reason;

    /**
     * 생성 일시
     */
    @Column
    private LocalDate createdAt;
}
