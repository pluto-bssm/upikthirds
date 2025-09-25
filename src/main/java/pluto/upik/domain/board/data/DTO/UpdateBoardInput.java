package pluto.upik.domain.board.data.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBoardInput {
    private String title;
    private String content;
}