package pluto.upik.shared.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import pluto.upik.shared.cache.CacheNames;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 기반 캐싱 설정 클래스.
 *
 * 고빈도 조회 데이터를 Redis에 저장하여 애플리케이션 메모리 의존도를 줄이고,
 * 다중 인스턴스 환경에서도 일관된 캐시를 제공합니다.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new JdkSerializationRedisSerializer()))
                .entryTtl(DEFAULT_TTL)
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CacheNames.BOARD_LIST, defaultConfig.entryTtl(Duration.ofMinutes(2)));
        cacheConfigurations.put(CacheNames.BOARD_USER, defaultConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put(CacheNames.BOARD_SEARCH, defaultConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put(CacheNames.BOARD_COMMENTS, defaultConfig.entryTtl(Duration.ofMinutes(1)));

        cacheConfigurations.put(CacheNames.VOTE_LIST, defaultConfig.entryTtl(Duration.ofSeconds(45)));
        cacheConfigurations.put(CacheNames.VOTE_DETAIL, defaultConfig.entryTtl(Duration.ofSeconds(30)));
        cacheConfigurations.put(CacheNames.VOTE_POPULAR, defaultConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put(CacheNames.VOTE_LEAST, defaultConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put(CacheNames.VOTE_MY, defaultConfig.entryTtl(Duration.ofSeconds(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
