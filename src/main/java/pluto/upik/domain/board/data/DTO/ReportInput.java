package pluto.upik.domain.board.data.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReportInput {
    private UUID targetId; // 신고 대상 ID (게시글 또는 댓글)
    private String reason; // 신고 사유
    private String detail; // 상세 내용
}