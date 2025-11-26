package pluto.upik.domain.voteResponse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.data.DTO.CreateVoteResponseInput;
import pluto.upik.domain.voteResponse.data.DTO.VoteResponsePayload;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.shared.cache.CacheNames;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoteResponseService {

    private final VoteResponseRepository voteResponseRepository;
    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final pluto.upik.domain.vote.service.VoteClosureService voteClosureService;

    /**
     * 사용자가 특정 투표에 참여했는지 확인합니다.
     *
     * userId가 null인 경우 (인증되지 않은 사용자) false를 반환합니다.
     *
     * @param userId 확인할 사용자 ID (null 가능)
     * @param voteId 투표 ID
     * @return 사용자가 투표에 참여했으면 true, 아니면 false
     */
    @Transactional(readOnly = true)
    public boolean hasUserVoted(UUID userId, UUID voteId) {
        if (userId == null) {
            return false;
        }
        return voteResponseRepository.findByUserIdAndVoteId(userId, voteId).isPresent();
    }


    @Caching(evict = {
            @CacheEvict(value = CacheNames.VOTE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_DETAIL, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_POPULAR, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_LEAST, allEntries = true),
            @CacheEvict(value = CacheNames.VOTE_MY, allEntries = true)
    })
    public VoteResponsePayload createVoteResponse(CreateVoteResponseInput input, UUID userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 2. 투표 조회
        Vote vote = voteRepository.findById(input.getVoteId())
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다: " + input.getVoteId()));

        // 3. 옵션 조회
        Option option = optionRepository.findById(input.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다: " + input.getOptionId()));

        log.info(user.toString());
        log.info(vote.toString());
        log.info(option.toString());

        // 4. 투표 상태 확인 (Status와 날짜 모두 확인)
        LocalDate currentDate = LocalDate.now();

        // 투표가 CLOSED 상태인 경우
        if (vote.getStatus() != Vote.Status.OPEN) {
            throw new IllegalStateException("투표가 종료되었습니다.");
        }

        // 투표 종료 날짜가 지난 경우
        if (vote.isFinishedByDate(currentDate)) {
            throw new IllegalStateException("투표 마감 기한이 지났습니다. (마감일: " + vote.getFinishedAt() + ")");
        }

        // 5. 옵션이 해당 투표에 속하는지 확인
        if (!option.getVote().getId().equals(vote.getId())) {
            throw new IllegalArgumentException("해당 옵션은 이 투표에 속하지 않습니다.");
        }

        // 6. 중복 투표 확인
        Optional<VoteResponse> existingResponse = voteResponseRepository
                .findByUserIdAndVoteId(userId, input.getVoteId());

        if (existingResponse.isPresent()) {
            throw new IllegalStateException("이미 이 투표에 참여하셨습니다.");
        }
        // 7. VoteResponse 생성 및 저장
        VoteResponse voteResponse = VoteResponse.builder()
                .user(user)
                .vote(vote)
                .selectedOption(option)
                .createdAt(LocalDate.now())
                .build();

        VoteResponse savedVoteResponse = voteResponseRepository.save(voteResponse);

        // 8. 참여자 수 기준 종료 조건 실시간 체크
        boolean wasClosed = voteClosureService.checkAndCloseVoteByParticipantCount(input.getVoteId());
        if (wasClosed) {
            log.info("투표 응답 저장 후 참여자 수 기준으로 투표 자동 종료: voteId={}", input.getVoteId());
        }

        // 9. 응답 반환
        return VoteResponsePayload.fromEntity(savedVoteResponse);
    }

    @Transactional(readOnly = true)
    public Long getVoteResponseCount(UUID voteId) {
        return voteResponseRepository.countByVoteId(voteId);
    }

    @Transactional(readOnly = true)
    public Long getOptionResponseCount(UUID optionId) {
        return voteResponseRepository.countByOptionId(optionId);
    }

    @Transactional(readOnly = true)
    public List<VoteResponsePayload> getMyVoteResponses(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID가 필요합니다.");
        }

        return voteResponseRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(VoteResponsePayload::fromEntity)
                .toList();
    }
}
