package pluto.upik.domain.search.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuery {
    private String query;
    private Integer limit;
    private Boolean includeAiSummary;
}