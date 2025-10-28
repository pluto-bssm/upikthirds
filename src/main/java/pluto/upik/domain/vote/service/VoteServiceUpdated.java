package pluto.upik.domain.vote.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.domain.voteResponse.service.VoteResponseService;
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

    // 더미 사용자 ID


    /**
     * 새로운 투표와 해당 옵션들을 생성하여 저장하고, 결과를 VotePayload로 반환합니다.
     *
     * @param input 생성할 투표의 제목, 카테고리, 옵션 목록을 포함한 입력 값
     * @return 생성된 투표와 옵션 정보를 담은 VotePayload 객체
     * @throws ResourceNotFoundException 더미 사용자를 찾을 수 없는 경우 발생
     */
    public VotePayload createVote(CreateVoteInput input,UUID userId) {
        // 더미 사용자 조회
        User dummyUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        // 1. Vote 엔티티 생성 (빌더에 user 정보 추가)
        Vote vote = Vote.builder()
                .id(UUID.randomUUID())
                .question(input.getTitle())
                .category(input.getCategory())
                .status(Vote.Status.OPEN)
                .finishedAt(LocalDate.now().plusDays(3)) // 예: 3일 뒤 종료
                .user(dummyUser) // 빌더에 user 정보 직접 추가
                .build();

        // 2. Vote 저장
        Vote savedVote = voteRepository.save(vote);

        // 3. Option들 생성
        List<Option> options = input.getOptions().stream().map(content ->
                Option.builder()
                        .vote(savedVote)
                        .content(content)
                        .build()
        ).toList();
        // 4. Option들 저장
        List<Option> savedOptions = optionRepository.saveAll(options);

        // 5. 정적 팩토리 메서드 사용하여 VotePayload 반환
        return VotePayload.fromEntity(savedVote, savedOptions);
    }


    /**
     * 주어진 사용자 ID에 대해 모든 투표 목록과 각 투표의 옵션별 통계, 사용자의 참여 여부를 반환합니다.
     *
     * @param userId 투표 참여 여부를 확인할 사용자 ID
     * @param includeExpired 종료된 투표 포함 여부 (true: 전체, false: 진행 중만)
     * @return 각 투표에 대한 옵션별 응답 수, 비율, 총 응답 수, 사용자의 투표 참여 여부가 포함된 VotePayload 리스트
     */
    @Transactional(readOnly = true)
    public List<VotePayload> getAllVotes(UUID userId, boolean includeExpired) {
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
                    option.getId(),
                    option.getContent(),
                    optionCount.intValue(),
                    percentage
                ));
            }

            // 사용자가 이 투표에 참여했는지 확인
            boolean hasVoted = voteResponseService.hasUserVoted(userId, vote.getId());

            votePayloads.add(VotePayload.fromEntityWithStats(
                vote,
                options,
                optionStats,
                totalResponses.intValue(),
                hasVoted
            ));
        }

        return votePayloads;
    }


    /**
     * 지정된 투표 ID와 사용자 ID로 투표 상세 정보를 조회합니다.
     *
     * 투표의 질문, 카테고리, 상태, 생성자, 마감일, 총 응답 수, 각 선택지별 응답 통계 및 사용자의 참여 여부를 포함한 상세 정보를 반환합니다.
     *
     * @param voteId 조회할 투표의 ID
     * @param userId 투표 참여 여부를 확인할 사용자 ID
     * @return 투표의 상세 정보를 담은 VoteDetailPayload 객체
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
                option.getId(),
                option.getContent(),
                optionCount.intValue(),
                percentage
            ));
        }

        String creatorName = null;
        if (vote.getUser() != null) {
            User creator = userRepository.findById(vote.getUser().getId())
                    .orElse(null);
            if (creator != null) {
                creatorName = creator.getUsername();
            }
        }

        // 사용자가 이 투표에 참여했는지 확인
        boolean hasVoted = voteResponseService.hasUserVoted(userId, voteId);

        return VoteDetailPayload.builder()
                .id(vote.getId())
                .title(vote.getQuestion())
                .category(vote.getCategory())
                .status(vote.getStatus().name())
                .createdBy(creatorName)
                .finishedAt(vote.getFinishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .totalResponses(totalResponses.intValue())
                .options(optionStats)
                .hasVoted(hasVoted)
                .build();
    }

    /**
     * 응답 수가 가장 많은 투표 3개를 조회하여 각 투표의 옵션별 통계와 함께 반환합니다.
     *
     * 각 투표에 대해 옵션별 응답 수와 비율을 계산하며, 사용자가 투표하지 않은 것으로 표시됩니다.
     *
     * @param includeExpired 종료된 투표 포함 여부 (true: 전체, false: OPEN 상태만)
     * @return 응답 수 기준 상위 3개 투표에 대한 옵션 통계가 포함된 VotePayload 리스트
     */
    @Transactional(readOnly = true)
    public List<VotePayload> getMostPopularOpenVote(boolean includeExpired) {
        LocalDate currentDate = LocalDate.now();
        List<Vote> votes;

        if (includeExpired) {
            votes = voteRepository.findAll();
        } else {
            // OPEN 상태이면서 날짜가 지나지 않은 투표만 조회
            List<Vote> openVotes = voteRepository.findByStatus(Vote.Status.OPEN);
            votes = openVotes.stream()
                .filter(vote -> !vote.isFinishedByDate(currentDate))
                .collect(Collectors.toList());
        }

        if (votes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Vote, Long> voteResponseCounts = new HashMap<>();
        for (Vote vote : votes) {
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
                    option.getId(),
                    option.getContent(),
                    optionCount.intValue(),
                    percentage
                ));
            }

            // 요청에 따라 투표하지 않은 것으로 표시
            result.add(VotePayload.fromEntityWithStats(vote, options, optionStats, totalResponses.intValue(), false));
        }

        return result;
    }

    /**
     * 응답 수가 가장 적은 투표를 조회하여 옵션별 통계와 함께 반환합니다.
     *
     * @param includeExpired 종료된 투표 포함 여부 (true: 전체, false: OPEN 상태만)
     * @return 응답 수가 가장 적은 투표의 통계 정보가 포함된 VotePayload 객체를 반환하며, 투표가 없으면 null을 반환합니다.
     */
    @Transactional(readOnly = true)
    public VotePayload getLeastPopularOpenVote(boolean includeExpired) {
        LocalDate currentDate = LocalDate.now();
        List<Vote> votes;

        if (includeExpired) {
            votes = voteRepository.findAll();
        } else {
            // OPEN 상태이면서 날짜가 지나지 않은 투표만 조회
            List<Vote> openVotes = voteRepository.findByStatus(Vote.Status.OPEN);
            votes = openVotes.stream()
                .filter(vote -> !vote.isFinishedByDate(currentDate))
                .collect(Collectors.toList());
        }

        if (votes.isEmpty()) {
            return null;
        }

        Map<Vote, Long> voteResponseCounts = new HashMap<>();
        for (Vote vote : votes) {
            Long responseCount = voteResponseRepository.countByVoteId(vote.getId());
            voteResponseCounts.put(vote, responseCount);
        }

        // 응답 수가 가장 적은 투표 찾기
        Map.Entry<Vote, Long> leastPopular = Collections.min(
            voteResponseCounts.entrySet(),
            Map.Entry.comparingByValue()
        );

        Vote vote = leastPopular.getKey();
        Long totalResponses = leastPopular.getValue();
        List<Option> options = optionRepository.findByVoteId(vote.getId());

        List<OptionWithStatsPayload> optionStats = new ArrayList<>();
        for (Option option : options) {
            Long optionCount = voteResponseRepository.countByOptionId(option.getId());
            float percentage = totalResponses > 0 ? (float) optionCount * 100 / totalResponses : 0;

            optionStats.add(new OptionWithStatsPayload(
                option.getId(),
                option.getContent(),
                optionCount.intValue(),
                percentage
            ));
        }

        // 요청에 따라 투표하지 않은 것으로 표시
        return VotePayload.fromEntityWithStats(vote, options, optionStats, totalResponses.intValue(), false);
    }
    
    /**
     * 지정한 사용자가 생성한 모든 투표와 각 투표의 옵션별 통계 정보를 조회합니다.
     *
     * @param userId 투표를 생성한 사용자의 UUID
     * @param includeExpired 종료된 투표 포함 여부 (true: 전체, false: 진행 중만)
     * @return 사용자가 생성한 투표 목록과 각 투표의 옵션별 응답 수, 비율, 총 응답 수, 사용자의 참여 여부가 포함된 리스트
     */
    @Transactional(readOnly = true)
    public List<VotePayload> getVotesByUserId(UUID userId, boolean includeExpired) {
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
                    option.getId(),
                    option.getContent(),
                    optionCount.intValue(),
                    percentage
                ));
            }

            // 사용자가 이 투표에 참여했는지 확인
            boolean hasVoted = voteResponseService.hasUserVoted(userId, vote.getId());

            votePayloads.add(VotePayload.fromEntityWithStats(
                vote,
                options,
                optionStats,
                totalResponses.intValue(),
                hasVoted
            ));
        }

        return votePayloads;
    }

    /**
     * 현재 사용자가 생성한 투표 목록을 반환합니다.
     *
     * @param userId 사용자 ID
     * @param includeExpired 종료된 투표 포함 여부 (true: 전체, false: 진행 중만)
     * @return 사용자가 생성한 투표 목록 리스트
     */
    @Transactional(readOnly = true)
    public List<VotePayload> getMyVotes(UUID userId, boolean includeExpired) {
        return getVotesByUserId(userId, includeExpired);
    }
}