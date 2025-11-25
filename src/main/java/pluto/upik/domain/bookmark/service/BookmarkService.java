package pluto.upik.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.bookmark.data.DTO.BookmarkResponse;
import pluto.upik.domain.bookmark.data.model.Bookmark;
import pluto.upik.domain.bookmark.repository.BookmarkRepository;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService implements BookmarkServiceInterface {

    private final BookmarkRepository bookmarkRepository;
    private final GuideRepository guideRepository;

    @Override
    public List<BookmarkResponse> getBookmarksByUserId(UUID userId) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(BookmarkResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuideResponse> getBookmarkedGuidesByUserId(UUID userId) {
        List<UUID> guideIds = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(Bookmark::getGuideId)
                .collect(Collectors.toList());

        if (guideIds.isEmpty()) {
            return new ArrayList<>();
        }

        return guideRepository.findAllById(guideIds)
                .stream()
                .map(guide -> GuideResponse.builder()
                        .id(guide.getId())
                        .title(guide.getTitle())
                        .content(guide.getContent())
                        .category(guide.getCategory())
                        .createdAt(guide.getCreatedAt())
                        .like(guide.getLike() != null ? guide.getLike().intValue() : 0) // Long -> Integer 변환
                        .voteId(guide.getVote() != null ? guide.getVote().getId() : null) // getVoteId() 대신 직접 접근
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean toggleBookmark(UUID userId, UUID guideId) {
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserIdAndGuideId(userId, guideId);

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return false; // 북마크 해제됨
        } else {
            Bookmark newBookmark = Bookmark.builder()
                    .userId(userId)
                    .guideId(guideId)
                    .build();
            bookmarkRepository.save(newBookmark);
            return true; // 북마크 추가됨
        }
    }

    @Override
    public boolean isBookmarked(UUID userId, UUID guideId) {
        return bookmarkRepository.existsByUserIdAndGuideId(userId, guideId);
    }

    @Override
    public long getBookmarkCount(UUID guideId) {
        return bookmarkRepository.countByGuideId(guideId);
    }

    @Override
    public List<GuideResponse> getGuidesSortedByBookmarkCount() {
        List<UUID> sortedGuideIds = bookmarkRepository.findGuideIdOrderByBookmarkCountDesc();

        if (sortedGuideIds.isEmpty()) {
            return guideRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10))
                    .stream()
                    .map(guide -> GuideResponse.builder()
                            .id(guide.getId())
                            .title(guide.getTitle())
                            .content(guide.getContent())
                            .category(guide.getCategory())
                            .createdAt(guide.getCreatedAt())
                            .like(guide.getLike() != null ? guide.getLike().intValue() : 0) // Long -> Integer 변환
                            .voteId(guide.getVote() != null ? guide.getVote().getId() : null) // getVoteId() 대신 직접 접근
                            .build())
                    .collect(Collectors.toList());
        }

        return guideRepository.findAllById(sortedGuideIds)
                .stream()
                .map(guide -> GuideResponse.builder()
                        .id(guide.getId())
                        .title(guide.getTitle())
                        .content(guide.getContent())
                        .category(guide.getCategory())
                        .createdAt(guide.getCreatedAt())
                        .like(guide.getLike() != null ? guide.getLike().intValue() : 0) // Long -> Integer 변환
                        .voteId(guide.getVote() != null ? guide.getVote().getId() : null) // getVoteId() 대신 직접 접근
                        .build())
                .collect(Collectors.toList());
    }

    public List<GuideResponse> getRecentGuides() {
        try {
        // Pageable 파라미터 추가
        Pageable pageable = PageRequest.of(0, 10); // 첫 페이지, 10개 항목
        return guideRepository.findAllByOrderByCreatedAtDesc(pageable)
                .getContent()
                .stream()
                .map(guide -> GuideResponse.builder()
                        .id(guide.getId())
                        .title(guide.getTitle())
                        .content(guide.getContent())
                        .category(guide.getCategory())
                        .createdAt(guide.getCreatedAt())
                            .like(guide.getLike() != null ? guide.getLike().intValue() : 0)
                            .voteId(guide.getVote() != null ? guide.getVote().getId() : null)
                        .build())
                .collect(Collectors.toList());
        } catch (Exception e) {
            // 오류 발생 시 빈 목록 반환
            return new ArrayList<>();
    }
}
}
