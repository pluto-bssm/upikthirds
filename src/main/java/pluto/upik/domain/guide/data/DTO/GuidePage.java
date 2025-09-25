package pluto.upik.domain.guide.data.DTO;


import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 페이징된 가이드 목록을 표현하는 DTO 클래스
 */
@Data
@Builder
public class GuidePage {
    private List<GuideResponse> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int size;
    private boolean hasNext;
}