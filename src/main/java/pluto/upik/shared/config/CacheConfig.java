package pluto.upik.shared.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 캐싱 설정 클래스
 *
 * Spring의 기본 ConcurrentMapCache를 사용한 인메모리 캐시를 설정합니다.
 * 자주 조회되는 데이터를 캐싱하여 데이터베이스 부하를 줄이고 성능을 향상시킵니다.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 기본 캐시 매니저 설정
     *
     * 캐시 이름:
     * - guides: 가이드 목록 캐싱
     * - votes: 투표 정보 캐싱
     * - users: 사용자 정보 캐싱
     * - options: 투표 옵션 캐싱
     * - voteResponses: 투표 응답 캐싱
     * - boards: 게시판 정보 캐싱
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "guides", "votes", "users", "options", "voteResponses", "boards"
        );
    }
}
