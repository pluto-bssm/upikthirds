package pluto.upik.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.bookmark.data.model.Bookmark;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<Bookmark> findByUserIdAndGuideId(UUID userId, UUID guideId);
    boolean existsByUserIdAndGuideId(UUID userId, UUID guideId);
    void deleteByUserIdAndGuideId(UUID userId, UUID guideId);

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.guideId = :guideId")
    long countByGuideId(UUID guideId);

    @Query("SELECT b.guideId FROM Bookmark b GROUP BY b.guideId ORDER BY COUNT(b.id) DESC")
    List<UUID> findGuideIdOrderByBookmarkCountDesc();
}