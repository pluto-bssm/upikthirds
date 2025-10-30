package pluto.upik.shared.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.tail.data.model.Tail;
import pluto.upik.domain.tail.repository.TailRepository;
import pluto.upik.domain.tail.repository.TailResponseRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * AI 서비스를 위한 비동기 헬퍼 클래스
 * 데이터베이스 조회를 병렬로 처리하여 성능을 최적화합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAIHelper {

    private final OptionRepository optionRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final TailRepository tailRepository;
    private final TailResponseRepository tailResponseRepository;

    /**
     * 투표 옵션을 비동기로 조회
     */
    @Async("aiTaskExecutor")
    public CompletableFuture<List<Option>> fetchOptionsAsync(Vote vote) {
        log.debug("비동기 옵션 조회 시작: voteId={}", vote.getId());
        List<Option> options = optionRepository.findByVoteId(vote.getId());
        return CompletableFuture.completedFuture(options);
    }

    /**
     * 투표 응답을 비동기로 조회
     */
    @Async("aiTaskExecutor")
    public CompletableFuture<List<VoteResponse>> fetchVoteResponsesAsync(Vote vote) {
        log.debug("비동기 투표 응답 조회 시작: voteId={}", vote.getId());
        List<VoteResponse> responses = voteResponseRepository.findByVoteId(vote.getId());
        return CompletableFuture.completedFuture(responses);
    }

    /**
     * 꼬리 질문을 비동기로 조회
     */
    @Async("aiTaskExecutor")
    public CompletableFuture<Optional<Tail>> fetchTailAsync(Vote vote) {
        log.debug("비동기 꼬리 질문 조회 시작: voteId={}", vote.getId());
        Optional<Tail> tail = tailRepository.findFirstByVote(vote);
        return CompletableFuture.completedFuture(tail);
    }

    /**
     * 꼬리 응답을 비동기로 조회
     */
    @Async("aiTaskExecutor")
    public CompletableFuture<List<String>> fetchTailResponsesAsync(Tail tail) {
        log.debug("비동기 꼬리 응답 조회 시작: tailId={}", tail.getId());
        List<String> answers = tailResponseRepository.findByTail(tail).stream()
                .map(tr -> tr.getAnswer())
                .toList();
        return CompletableFuture.completedFuture(answers);
    }

    /**
     * 투표 통계를 계산 (옵션별 득표율)
     */
    public Map<UUID, Double> calculateVoteStatistics(List<Option> options, List<VoteResponse> voteResponses) {
        Map<UUID, Long> voteCounts = new HashMap<>();
        long totalVotes = voteResponses.size();

        for (VoteResponse vr : voteResponses) {
            voteCounts.merge(vr.getSelectedOption().getId(), 1L, Long::sum);
        }

        Map<UUID, Double> statistics = new HashMap<>();
        for (Option option : options) {
            long count = voteCounts.getOrDefault(option.getId(), 0L);
            double percent = totalVotes > 0 ? (count * 100.0 / totalVotes) : 0.0;
            statistics.put(option.getId(), percent);
        }

        return statistics;
    }

    /**
     * 옵션 통계 문자열 생성
     */
    public String formatOptionsWithPercents(List<Option> options, Map<UUID, Double> statistics) {
        StringBuilder builder = new StringBuilder();
        for (Option option : options) {
            double percent = statistics.getOrDefault(option.getId(), 0.0);
            builder.append(option.getContent())
                    .append(" - ")
                    .append(String.format("%.1f", percent))
                    .append("%\n");
        }
        return builder.toString().trim();
    }
}
