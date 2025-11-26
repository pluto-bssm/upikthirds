package pluto.upik.domain.vote.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class VoteService {

    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final UserRepository userRepository;
    private final VoteResponseService voteResponseService;
    private final TailRepository tailRepository;

    // 더미 사용자 ID
    private static final UUID DUMMY_USER_ID = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");

    /**
     * 새로운 투표와 해당 선택지를 생성하고 저장한 후, 결과를 VotePayload로 반환합니다.
     *
     * @param input 생성할 투표의 제목, 카테고리, 선택지 목록을 포함한 입력값
     * @return 생성된 투표와 선택지 정보를 담은 VotePayload 객체
     */
    public VotePayload createVote(CreateVoteInput input) {
        // 1. Vote 엔티티 생성
        Vote vote = Vote.builder()
                .id(UUID.randomUUID())
                .question(input.getTitle())
                .category(input.getCategory())
                .status(Vote.Status.OPEN)
                .finishedAt(LocalDate.now().plusDays(3)) // 예: 3일 뒤 종료
                .build();

        // 2. Vote 저장
        Vote savedVote = voteRepository.save(vote);

        // 3. Option들 생성
        List<Option> options = input.getOptions().stream().map(content -> Option.builder()
                .vote(savedVote)
                .content(content)
                .build()).toList();
        // 4. Option들 저장
        List<Option> savedOptions = optionRepository.saveAll(options);

        // 5. 정적 팩토리 메서드 사용하여 VotePayload 반환
        return VotePayload.fromEntity(savedVote, savedOptions);
    }

    /**
     * 모든 투표 목록을 조회하여 반환합니다.
     *
     * 사용자 참여 여부는 더미 사용자 ID를 기준으로 판단됩니다.
     *
     * @return 전체 투표에 대한 VotePayload 리스트
     */
    @Transactional(readOnly = true)
    public List<VotePayload> getAllVotes() {
        return getAllVotes(DUMMY_USER_ID);
    }

    /**
     * 지정된 사용자의 투표 참여 여부와 각 투표의 통계 정보를 포함하여 모든 투표 목록을 반환합니다.
     *
     * @param userId 투표 참여 여부를 확인할 사용자 ID
     * @return 각 투표의 옵션별 통계, 총 응답 수, 사용자의 참여 여부를 포함한 투표 페이로드 리스트
     */
    @Transactional(readOnly = true)
    public List<VotePayload> getAllVotes(UUID userId) {
        List<Vote> votes = voteRepository.findAll();
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
                        percentage));
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
                    userResponse.map(vr -> vr.getSelectedOption().getContent()).orElse(null)));
        }

        return votePayloads;
    }

    /**
     * 지정된 투표 ID에 해당하는 투표의 상세 정보를 반환합니다.
     * 
     * 사용자 참여 여부는 기본 더미 사용자 ID를 기준으로 판단됩니다.
     *
     * @param voteId 조회할 투표의 UUID
     * @return 투표 상세 정보를 담은 VoteDetailPayload 객체
     */
    @Transactional(readOnly = true)
    public VoteDetailPayload getVoteById(UUID voteId) {
        return getVoteById(voteId, DUMMY_USER_ID);
    }

    /**
     * 지정된 투표 ID와 사용자 ID로 투표 상세 정보를 조회하여 반환합니다.
     *
     * 투표의 질문, 카테고리, 상태, 생성자 이름, 마감일, 전체 응답 수, 각 선택지별 통계(응답 수 및 비율),
     * 그리고 해당 사용자의 투표 참여 여부를 포함한 상세 정보를 제공합니다.
     *
     * @param voteId 조회할 투표의 ID
     * @param userId 투표 참여 여부를 확인할 사용자 ID
     * @return 투표 상세 정보를 담은 VoteDetailPayload 객체
     * @throws ResourceNotFoundException 투표가 존재하지 않을 경우 발생
     */
    @Transactional(readOnly = true)
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
                    percentage));
        }

        String creatorName = null;
        if (vote.getUser() != null) {
            User creator = userRepository.findById(vote.getUser().getId())
                    .orElse(null);
            if (creator != null) {
                creatorName = creator.getUsername();
            }
        }

        Optional<VoteResponse> userResponse = findUserResponse(userId, voteId);
        boolean hasVoted = userResponse.isPresent();
        Optional<Tail> tail = tailRepository.findFirstByVote(vote);

        return VoteDetailPayload.builder()
                .id(vote.getId().toString())
                .title(vote.getQuestion())
                .category(vote.getCategory())
                .status(vote.getStatus().name())
                .createdBy(creatorName)
                .finishedAt(vote.getFinishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .closureType(
                        vote.getClosureType() != null ? vote.getClosureType().name() : Vote.ClosureType.DEFAULT.name())
                .participantThreshold(vote.getParticipantThreshold())
                .totalResponses(totalResponses.intValue())
                .options(optionStats)
                .hasVoted(hasVoted)
                .myOptionId(userResponse.map(vr -> vr.getSelectedOption().getId().toString()).orElse(null))
                .myOptionContent(userResponse.map(vr -> vr.getSelectedOption().getContent()).orElse(null))
                .tailId(tail.map(t -> t.getId().toString()).orElse(null))
                .tailQuestion(tail.map(Tail::getQuestion).orElse(null))
                .build();
    }

    /**
     * 응답 수가 가장 많은 OPEN 상태의 투표 3개를 조회하여 각 투표의 통계 정보를 반환합니다.
     *
     * 각 투표에 대해 옵션별 응답 수, 응답 비율, 전체 응답 수, 사용자의 투표 여부(항상 false)를 포함한 정보를 제공합니다.
     *
     * @return 응답 수 기준 상위 3개의 OPEN 투표에 대한 통계 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<VotePayload> getMostPopularOpenVote() {
        List<Vote> openVotes = voteRepository.findByStatus(Vote.Status.OPEN);
        if (openVotes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Vote, Long> voteResponseCounts = new HashMap<>();
        for (Vote vote : openVotes) {
            Long responseCount = voteResponseRepository.countByVoteId(vote.getId());
            voteResponseCounts.put(vote, responseCount);
        }

        // 응답 수가 많은 순으로 정렬
        List<Map.Entry<Vote, Long>> sortedVotes = voteResponseCounts.entrySet().stream()
                .sorted(Map.Entry.<Vote, Long>comparingByValue().reversed())
                .limit(3) // 상위 3개만 선택
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
                        percentage));
            }

            // 요청에 따라 투표하지 않은 것으로 표시
            result.add(VotePayload.fromEntityWithStats(vote, options, optionStats, totalResponses.intValue(), false));
        }

        return result;
    }

    /**
     * 응답 수가 가장 적은 OPEN 상태의 투표를 조회하여 통계와 함께 반환합니다.
     *
     * 투표가 없을 경우 null을 반환하며, 반환되는 투표에는 각 선택지별 응답 수와 비율, 전체 응답 수, 사용자의 참여 여부(항상
     * false)가 포함됩니다.
     *
     * @return 응답 수가 가장 적은 OPEN 상태 투표의 통계 정보가 담긴 VotePayload, 투표가 없으면 null
     */
    @Transactional(readOnly = true)
    public VotePayload getLeastPopularOpenVote() {
        List<Vote> openVotes = voteRepository.findByStatus(Vote.Status.OPEN);
        if (openVotes.isEmpty()) {
            return null;
        }

        Map<Vote, Long> voteResponseCounts = new HashMap<>();
        for (Vote vote : openVotes) {
            Long responseCount = voteResponseRepository.countByVoteId(vote.getId());
            voteResponseCounts.put(vote, responseCount);
        }

        // 응답 수가 가장 적은 투표 찾기
        Map.Entry<Vote, Long> leastPopular = Collections.min(
                voteResponseCounts.entrySet(),
                Map.Entry.comparingByValue());

        Vote vote = leastPopular.getKey();
        Long totalResponses = leastPopular.getValue();
        List<Option> options = optionRepository.findByVoteId(vote.getId());

        List<OptionWithStatsPayload> optionStats = new ArrayList<>();
        for (Option option : options) {
            Long optionCount = voteResponseRepository.countByOptionId(option.getId());
            float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

            optionStats.add(new OptionWithStatsPayload(
                    option.getId().toString(),
                    option.getContent(),
                    optionCount.intValue(),
                    percentage));
        }

        // 요청에 따라 투표하지 않은 것으로 표시
        return VotePayload.fromEntityWithStats(vote, options, optionStats, totalResponses.intValue(), false);
    }

    /**
     * 투표 목록을 정렬하여 가져옵니다.
     *
     * @param sortBy 정렬 기준 ("date", "participation", "completion")
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @return 정렬된 투표 목록
     */
    public List<VotePayload> getVotesSorted(String sortBy, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vote> votesPage;

        switch (sortBy.toLowerCase()) {
            case "participation":
                // 참여율 기준 정렬
                votesPage = voteRepository.findAllOrderByParticipationRate(pageable);
                break;
            case "completion":
                // 종료율 기준 정렬
                votesPage = voteRepository.findAllOrderByCompletionRate(pageable);
                break;
            case "date":
            default:
                // 기본: 생성일 기준 정렬 (최신순)
                votesPage = voteRepository.findAllByOrderByCreatedAtDesc(pageable);
                break;
        }

        return votesPage.getContent().stream()
                .map(vote -> {
                    // 더미 옵션 목록 생성
                    List<Option> dummyOptions = new ArrayList<>();
                    // fromEntity 메서드 사용
                return VotePayload.fromEntity(vote, dummyOptions);
            })
                .collect(Collectors.toList());
    }

    private Optional<VoteResponse> findUserResponse(UUID userId, UUID voteId) {
        if (userId == null) {
            return Optional.empty();
        }
        return voteResponseRepository.findByUserIdAndVoteId(userId, voteId);
    }
}
