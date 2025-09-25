package pluto.upik.domain.board.data.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardInput {
    private String title;
    private String content;
}