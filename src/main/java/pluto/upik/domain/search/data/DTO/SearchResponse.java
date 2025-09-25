package pluto.upik.domain.search.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private String query;
    private List<KeywordGuideResponse> guides;
    private String aiSummary;
    private Integer totalResults;
    private Integer searchTimeMs; // Long에서 Integer로 변경
}