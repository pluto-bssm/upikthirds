package pluto.upik.domain.revote.data.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pluto.upik.domain.revote.data.model.RevoteRequest;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재투표 요청 응답 DTO
 *
 * 재투표 요청 정보를 클라이언트에게 전달하는 DTO입니다.
 *
 * @author upik-team
 * @version 2.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevoteRequestResponse {

    /**
     * 재투표 요청 ID
     */
    private Long id;

    /**
     * 요청한 사용자 ID
     */
    private UUID userId;

    /**
     * 재투표가 요청된 가이드 ID
     */
    private UUID guideId;

    /**
     * 재투표 요청 이유
     */
    private String reason;

    /**
     * 재투표 요청 상세 이유
     */
    private String detailReason;

    /**
     * 재투표 요청 상태
     */
    private String status;

    /**
     * 재투표 요청 상태 설명
     */
    private String statusDescription;

    /**
     * 재투표 요청 생성 일시
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 재투표 요청 수정 일시
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * RevoteRequest 엔티티를 RevoteRequestResponse DTO로 변환합니다.
     *
     * @param revoteRequest 변환할 재투표 요청 엔티티
     * @return RevoteRequestResponse 변환된 DTO
     * @throws IllegalArgumentException revoteRequest가 null인 경우
     */
    public static RevoteRequestResponse fromEntity(RevoteRequest revoteRequest) {
        if (revoteRequest == null) {
            throw new IllegalArgumentException("RevoteRequest는 null일 수 없습니다.");
        }

        return RevoteRequestResponse.builder()
                .id(revoteRequest.getId())
                .userId(revoteRequest.getUserId())
                .guideId(revoteRequest.getGuideId())
                .reason(revoteRequest.getReason())
                .detailReason(revoteRequest.getDetailReason())
                .status(revoteRequest.getStatus().name())
                .statusDescription(revoteRequest.getStatus().getDescription())
                .createdAt(revoteRequest.getCreatedAt())
                .updatedAt(revoteRequest.getUpdatedAt())
                .build();
    }

    /**
     * 요청이 대기중 상태인지 확인합니다.
     *
     * @return boolean 대기중 상태 여부
     */
    public boolean isPending() {
        return RevoteRequest.RevoteRequestStatus.PENDING.name().equals(this.status);
    }

    /**
     * 요청이 승인된 상태인지 확인합니다.
     *
     * @return boolean 승인된 상태 여부
     */
    public boolean isApproved() {
        return RevoteRequest.RevoteRequestStatus.APPROVED.name().equals(this.status);
    }

    /**
     * 요청이 거부된 상태인지 확인합니다.
     *
     * @return boolean 거부된 상태 여부
     */
    public boolean isRejected() {
        return RevoteRequest.RevoteRequestStatus.REJECTED.name().equals(this.status);
    }
}