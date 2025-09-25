package pluto.upik.domain.bookmark.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pluto.upik.domain.bookmark.data.model.Bookmark;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private UUID id; // Long에서 UUID로 변경
    private UUID userId;
    private UUID guideId;
    private LocalDate createdAt; // LocalDateTime에서 LocalDate로 변경 (데이터베이스 스키마와 일치)
    public static BookmarkResponse fromEntity(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId()) // 이제 UUID 타입이므로 호환됨
                .userId(bookmark.getUserId())
                .guideId(bookmark.getGuideId())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}