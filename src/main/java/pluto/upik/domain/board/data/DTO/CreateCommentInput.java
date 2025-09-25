package pluto.upik.domain.board.data.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCommentInput {
    private String content;
    private UUID boardId;
    private UUID parentId; // 대댓글인 경우 부모 댓글 ID
}