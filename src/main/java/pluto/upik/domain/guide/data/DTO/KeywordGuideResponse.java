package pluto.upik.domain.guide.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 키워드 검색 결과 가이드 정보를 클라이언트에 전달하기 위한 DTO 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KeywordGuideResponse {
    /**
     * 가이드 ID
     */
    private UUID id;
    
    /**
     * 가이드 제목
     */
    private String title;
    
    /**
     * 검색 키워드
     */
    private String keyword;
    
    /**
     * 가이드 내용
     */
    private String content;
    
    /**
     * 가이드 생성 일자
     */
    private LocalDate createdAt;
    
    /**
     * 가이드 타입
     */
    private String guideType;

    /**
     * 카테고리
     */
    private String category;
    /**
     * 좋아요 수
     */
    private Long likeCount;

    /**
     * 재투표 수
     */
    private Long revoteCount;

    /**
     * 작성자 ID
     */
    private UUID userId;

    /**
     * 작성자 이름
     */
    private String userName;

    /**
     * 작성자 프로필 이미지 URL
     */
    private String userProfileImage;

    /**
     * 작성자 이메일
     */
    private String userEmail;

    /**
     * 가이드 내용을 지정한 최대 길이로 요약하여 반환합니다.
     *
     * 내용이 null이거나 최대 길이 이하인 경우 전체 내용을 반환하며, 그렇지 않으면 최대 길이만큼 잘라 "..."을 덧붙여 반환합니다.
     *
     * @param maxLength 반환할 내용의 최대 문자 수
     * @return 요약된 가이드 내용 또는 전체 내용
     */
    public String getContentSummary(int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
}
        return content.substring(0, maxLength) + "...";
    }

    /**
     * 제목에서 키워드와 일치하는 부분을 모두 대소문자 구분 없이 찾아 &lt;strong&gt; 태그로 감싸 하이라이트된 문자열을 반환합니다.
     *
     * 제목이나 키워드가 null이거나 키워드가 비어 있으면 원본 제목을 그대로 반환합니다.
     *
     * @return 키워드가 강조된 제목 문자열
     */
    public String getHighlightedTitle() {
        if (title == null || keyword == null || keyword.isEmpty()) {
            return title;
}
        return title.replaceAll("(?i)" + keyword, "<strong>" + keyword + "</strong>");
    }
}
