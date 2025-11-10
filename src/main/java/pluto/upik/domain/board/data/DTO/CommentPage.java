package pluto.upik.domain.board.data.DTO;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class CommentPage implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CommentResponse> content;
    private int totalPages;
    private long totalElements;
}
