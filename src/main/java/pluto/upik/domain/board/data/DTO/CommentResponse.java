package pluto.upik.domain.board.data.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CommentResponse {
    private UUID id;
    private String content;
    private UUID userId;
    private String userName; // 사용자 이름 필드 추가
    private UUID boardId;
    private UUID parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> replies;
}