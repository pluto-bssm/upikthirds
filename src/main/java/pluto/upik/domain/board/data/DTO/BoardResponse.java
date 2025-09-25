package pluto.upik.domain.board.data.DTO;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BoardResponse {
    private UUID id;
    private String title;
    private String content;
    private UUID userId;
    private String userName; // 사용자 이름 필드 유지
    // userProfileImage 필드 제거
    private int viewCount;
    private long commentCount;
    private long bookmarkCount;
    private boolean isBookmarked; // 북마크 여부 필드
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}