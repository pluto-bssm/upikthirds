package pluto.upik.domain.board.data.DTO;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class BoardPage {
    private List<BoardResponse> content;
    private int totalPages;
    private long totalElements;
}