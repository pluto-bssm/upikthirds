package pluto.upik.domain.bookmark.service;

import pluto.upik.domain.bookmark.data.DTO.BookmarkResponse;
import pluto.upik.domain.guide.data.DTO.GuideResponse;

import java.util.List;
import java.util.UUID;

public interface BookmarkServiceInterface {
    List<BookmarkResponse> getBookmarksByUserId(UUID userId);
    List<GuideResponse> getBookmarkedGuidesByUserId(UUID userId);
    boolean toggleBookmark(UUID userId, UUID guideId);
    boolean isBookmarked(UUID userId, UUID guideId);
    long getBookmarkCount(UUID guideId);
    List<GuideResponse> getGuidesSortedByBookmarkCount();
}