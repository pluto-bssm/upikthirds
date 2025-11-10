package pluto.upik.domain.vote.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionWithStatsPayload implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String content;
    private int responseCount;
    private float percentage;
}
