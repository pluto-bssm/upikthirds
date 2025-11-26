package pluto.upik.domain.vote.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.vote.data.DTO.CreateVoteInput;
import pluto.upik.domain.vote.data.DTO.OptionWithStatsPayload;
import pluto.upik.domain.vote.data.DTO.VoteDetailPayload;
import pluto.upik.domain.vote.data.DTO.VotePayload;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.domain.voteResponse.service.VoteResponseService;
import pluto.upik.domain.tail.data.model.Tail;
import pluto.upik.domain.tail.repository.TailRepository;
import pluto.upik.domain.tail.repository.TailResponseRepository;
import pluto.upik.shared.cache.CacheNames;
import pluto.upik.shared.exception.ResourceNotFoundException;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoteServiceUpdated {

    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final UserRepository userRepository;
    private final VoteResponseService voteResponseService;
    private final TailRepository tailRepository;
    private final TailResponseRepository tailResponseRepository;

    @Caching(evict = {
            @CacheEvict(value = CacheNames.VOTE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_DETAIL, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_POPULAR, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_LEAST, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_MY, allEntries = true)
    })
    public VotePayload createVote(CreateVoteInput input, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        Vote vote = Vote.builder()
                .id(UUID.randomUUID())
                .question(input.getTitle())
                .category(input.getCategory())
                .status(Vote.Status.OPEN)
                .finishedAt(LocalDate.now().plusDays(3))
                .user(user)
                .build();

        Vote savedVote = voteRepository.save(vote);

        List<Option> options = input.getOptions().stream().map(content ->
                Option.builder()
                        .vote(savedVote)
                        .content(content)
                        .build()
        ).toList();
        List<Option> savedOptions = optionRepository.saveAll(options);

        return VotePayload.fromEntity(savedVote, savedOptions);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VOTE_LIST, key = "T(java.lang.String).format('%s:%b:%b', #userId, #includeExpired, #includeHasVoted)")
    public List<VotePayload> getAllVotes(UUID userId, boolean includeExpired, boolean includeHasVoted) {
        LocalDate currentDate = LocalDate.now();
        List<Vote> votes;

        if (includeExpired) {
            votes = voteRepository.findAll();
        } else {
            votes = voteRepository.findActiveVotes(currentDate);
        }

        List<VotePayload> votePayloads = new ArrayList<>();

        for (Vote vote : votes) {
            List<Option> options = optionRepository.findByVoteId(vote.getId());
            Long totalResponses = voteResponseRepository.countByVoteId(vote.getId());
            List<OptionWithStatsPayload> optionStats = new ArrayList<>();
            for (Option option : options) {
                Long optionCount = voteResponseRepository.countByOptionId(option.getId());
                float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

                optionStats.add(new OptionWithStatsPayload(
                    option.getId().toString(),
                    option.getContent(),
                    optionCount.intValue(),
                    percentage
                ));
            }

            Optional<VoteResponse> userResponse = findUserResponse(userId, vote.getId());
            boolean hasVoted = userResponse.isPresent();

            votePayloads.add(VotePayload.fromEntityWithStats(
                vote,
                options,
                optionStats,
                totalResponses.intValue(),
                hasVoted,
                userResponse.map(vr -> vr.getSelectedOption().getId().toString()).orElse(null),
                userResponse.map(vr -> vr.getSelectedOption().getContent()).orElse(null)
            ));
        }

        if (includeHasVoted) {
            return votePayloads.stream()
                    .filter(p -> !p.getHasVoted())
                    .collect(Collectors.toList());
        }

        return votePayloads;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VOTE_DETAIL, key = "T(java.lang.String).format('%s:%s', #voteId, T(java.util.Objects).toString(#userId))")
    public VoteDetailPayload getVoteById(UUID voteId, UUID userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("투표를 찾을 수 없습니다: " + voteId));

        List<Option> options = optionRepository.findByVoteId(voteId);
        Long totalResponses = voteResponseRepository.countByVoteId(voteId);

        List<OptionWithStatsPayload> optionStats = new ArrayList<>();
        for (Option option : options) {
            Long optionCount = voteResponseRepository.countByOptionId(option.getId());
            float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

            optionStats.add(new OptionWithStatsPayload(
                option.getId().toString(),
                option.getContent(),
                optionCount.intValue(),
                percentage
            ));
        }

        String creatorName = Optional.ofNullable(vote.getUser())
                .map(User::getUsername)
                .orElse(null);

        Optional<VoteResponse> userResponse = findUserResponse(userId, voteId);
        boolean hasVoted = userResponse.isPresent();
        Optional<Tail> tail = tailRepository.findFirstByVote(vote);
        Optional<pluto.upik.domain.tail.data.model.TailResponse> myTailResponse =
                (userId != null) ? tailResponseRepository.findByUserIdAndVoteId(userId, voteId) : Optional.empty();

        return VoteDetailPayload.builder()
                .id(vote.getId().toString())
                .title(vote.getQuestion())
                .category(vote.getCategory())
                .status(vote.getStatus().name())
                .createdBy(creatorName)
                .finishedAt(vote.getFinishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .closureType(vote.getClosureType() != null ? vote.getClosureType().name() : Vote.ClosureType.DEFAULT.name())
                .participantThreshold(vote.getParticipantThreshold())
                .totalResponses(totalResponses.intValue())
                .options(optionStats)
                .hasVoted(hasVoted)
                .myOptionId(userResponse.map(vr -> vr.getSelectedOption().getId().toString()).orElse(null))
                .myOptionContent(userResponse.map(vr -> vr.getSelectedOption().getContent()).orElse(null))
                .tailId(tail.map(t -> t.getId().toString()).orElse(null))
                .tailQuestion(tail.map(Tail::getQuestion).orElse(null))
                .myTailId(myTailResponse.map(tr -> tr.getTail().getId().toString()).orElse(null))
                .myTailQuestion(myTailResponse.map(tr -> tr.getTail().getQuestion()).orElse(null))
                .myTailAnswer(myTailResponse.map(pluto.upik.domain.tail.data.model.TailResponse::getAnswer).orElse(null))
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VOTE_POPULAR, key = "T(java.lang.String).format('%s:%b:%b', #userId, #includeExpired, #includeHasVoted)")
    public List<VotePayload> getMostPopularOpenVote(UUID userId, boolean includeExpired, boolean includeHasVoted) {
        LocalDate currentDate = LocalDate.now();
        List<Vote> votes;

        if (includeExpired) {
            votes = voteRepository.findAll();
        } else {
            votes = voteRepository.findActiveVotes(currentDate);
        }

        if (votes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Vote, Long> voteResponseCounts = new HashMap<>();
        for (Vote vote : votes) {
            Long responseCount = voteResponseRepository.countByVoteId(vote.getId());
            voteResponseCounts.put(vote, responseCount);
        }

        List<Map.Entry<Vote, Long>> sortedVotes = voteResponseCounts.entrySet().stream()
            .sorted(Map.Entry.<Vote, Long>comparingByValue().reversed())
            .limit(3)
            .collect(Collectors.toList());

        List<VotePayload> result = new ArrayList<>();
        for (Map.Entry<Vote, Long> entry : sortedVotes) {
            Vote vote = entry.getKey();
            Long totalResponses = entry.getValue();
            List<Option> options = optionRepository.findByVoteId(vote.getId());

            List<OptionWithStatsPayload> optionStats = new ArrayList<>();
            for (Option option : options) {
                Long optionCount = voteResponseRepository.countByOptionId(option.getId());
                float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

                optionStats.add(new OptionWithStatsPayload(
                    option.getId().toString(),
                    option.getContent(),
                    optionCount.intValue(),
                    percentage
                ));
            }

            Optional<VoteResponse> userResponse = findUserResponse(userId, vote.getId());
            boolean hasVoted = userResponse.isPresent();
            result.add(VotePayload.fromEntityWithStats(
                    vote,
                    options,
                    optionStats,
                    totalResponses.intValue(),
                    hasVoted,
                    userResponse.map(vr -> vr.getSelectedOption().getId().toString()).orElse(null),
                    userResponse.map(vr -> vr.getSelectedOption().getContent()).orElse(null)));
        }

        if (includeHasVoted) {
            return result.stream()
                    .filter(p -> !p.getHasVoted())
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VOTE_LEAST, key = "T(java.lang.String).format('%s:%b:%b', #userId, #includeExpired, #includeHasVoted)")
    public VotePayload getLeastPopularOpenVote(UUID userId, boolean includeExpired, boolean includeHasVoted) {
        LocalDate currentDate = LocalDate.now();
        List<Vote> votes;

        if (includeExpired) {
            votes = voteRepository.findAll();
        } else {
            votes = voteRepository.findActiveVotes(currentDate);
        }

        if (votes.isEmpty()) {
            return null;
        }

        Map<Vote, Long> voteResponseCounts = new HashMap<>();
        for (Vote vote : votes) {
            Long responseCount = voteResponseRepository.countByVoteId(vote.getId());
            voteResponseCounts.put(vote, responseCount);
        }

        Optional<Map.Entry<Vote, Long>> leastPopular = voteResponseCounts.entrySet().stream()
            .min(Map.Entry.comparingByValue());

        if (leastPopular.isEmpty()) {
            return null;
        }

        Vote vote = leastPopular.get().getKey();
        Long totalResponses = leastPopular.get().getValue();
        List<Option> options = optionRepository.findByVoteId(vote.getId());

        List<OptionWithStatsPayload> optionStats = new ArrayList<>();
        for (Option option : options) {
            Long optionCount = voteResponseRepository.countByOptionId(option.getId());
            float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

            optionStats.add(new OptionWithStatsPayload(
                option.getId().toString(),
                option.getContent(),
                optionCount.intValue(),
                percentage
            ));
        }

        Optional<VoteResponse> userResponse = findUserResponse(userId, vote.getId());
        boolean hasVoted = userResponse.isPresent();
        VotePayload payload = VotePayload.fromEntityWithStats(
                vote,
                options,
                optionStats,
                totalResponses.intValue(),
                hasVoted,
                userResponse.map(vr -> vr.getSelectedOption().getId().toString()).orElse(null),
                userResponse.map(vr -> vr.getSelectedOption().getContent()).orElse(null));

        if (includeHasVoted && payload.getHasVoted()) {
            return null;
        }

        return payload;
    }

    @Transactional(readOnly = true)
    public List<VotePayload> getVotesByUserId(UUID userId, boolean includeExpired, boolean includeHasVoted) {
        LocalDate currentDate = LocalDate.now();
        List<Vote> votes;

        if (includeExpired) {
            votes = voteRepository.findByUserId(userId);
        } else {
            votes = voteRepository.findActiveVotesByUserId(userId, currentDate);
        }

        List<VotePayload> votePayloads = new ArrayList<>();

        for (Vote vote : votes) {
            List<Option> options = optionRepository.findByVoteId(vote.getId());
            Long totalResponses = voteResponseRepository.countByVoteId(vote.getId());
            List<OptionWithStatsPayload> optionStats = new ArrayList<>();
            for (Option option : options) {
                Long optionCount = voteResponseRepository.countByOptionId(option.getId());
                float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

                optionStats.add(new OptionWithStatsPayload(
                    option.getId().toString(),
                    option.getContent(),
                    optionCount.intValue(),
                    percentage
                ));
            }

            Optional<VoteResponse> userResponse = findUserResponse(userId, vote.getId());
            boolean hasVoted = userResponse.isPresent();

            votePayloads.add(VotePayload.fromEntityWithStats(
                vote,
                options,
                optionStats,
                totalResponses.intValue(),
                hasVoted,
                userResponse.map(vr -> vr.getSelectedOption().getId().toString()).orElse(null),
                userResponse.map(vr -> vr.getSelectedOption().getContent()).orElse(null)
            ));
        }

        if (includeHasVoted) {
            return votePayloads.stream()
                    .filter(p -> !p.getHasVoted())
                    .collect(Collectors.toList());
        }

        return votePayloads;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.VOTE_MY, key = "T(java.lang.String).format('%s:%b:%b', #userId, #includeExpired, #includeHasVoted)")
    public List<VotePayload> getMyVotes(UUID userId, boolean includeExpired, boolean includeHasVoted) {
        return getVotesByUserId(userId, includeExpired, includeHasVoted);
    }

    private Optional<VoteResponse> findUserResponse(UUID userId, UUID voteId) {
        if (userId == null) {
            return Optional.empty();
        }
        return voteResponseRepository.findByUserIdAndVoteId(userId, voteId);
    }
}
