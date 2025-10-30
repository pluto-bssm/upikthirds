package pluto.upik.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리를 위한 설정 클래스
 *
 * ThreadPoolTaskExecutor를 사용하여 비동기 작업의 스레드 풀을 관리합니다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * AI 서비스용 비동기 실행기
     * - 코어 풀 크기: 5
     * - 최대 풀 크기: 10
     * - 큐 용량: 100
     */
    @Bean(name = "aiTaskExecutor")
    public Executor aiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ai-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * 일반 비즈니스 로직용 비동기 실행기
     * - 코어 풀 크기: 10
     * - 최대 풀 크기: 20
     * - 큐 용량: 200
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("app-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * 데이터베이스 배치 작업용 비동기 실행기
     * - 코어 풀 크기: 8
     * - 최대 풀 크기: 15
     * - 큐 용량: 150
     */
    @Bean(name = "dbBatchExecutor")
    public Executor dbBatchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(150);
        executor.setThreadNamePrefix("db-batch-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
