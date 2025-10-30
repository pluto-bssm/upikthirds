package pluto.upik.domain.vote.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.vote.data.DTO.CreateVoteInput;
import pluto.upik.domain.vote.data.DTO.VotePayload;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.shared.exception.BadWordException;
import pluto.upik.shared.filter.BadWordFilterService;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteApplication {

    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final BadWordFilterService badWordFilterService;

    /**
     * 새로운 투표를 생성합니다.
     *
     * @param input 투표 생성 입력 데이터
     * @return 생성된 투표 정보
     */
    @Transactional
    public VotePayload createVote(CreateVoteInput input,UUID userId) {
        // 욕설 검증
        if (badWordFilterService.containsBadWord(input.getTitle())) {
            throw BadWordException.Predefined.inTitle();
        }
        if (badWordFilterService.containsBadWord(input.getCategory())) {
            throw BadWordException.Predefined.inCategory();
        }
        if (badWordFilterService.containsBadWordInList(input.getOptions())) {
            throw BadWordException.Predefined.inOptions();
        }

        // 종료 조건 검증
        validateClosureConditions(input);

        // 현재는 테스트를 위해 첫 번째 사용자를 가져옴 (실제로는 인증된 사용자를 사용해야 함)
        User user = userRepository.findById(userId).orElseThrow();

        // 종료 타입 결정 (기본값: DEFAULT)
        Vote.ClosureType closureType = Vote.ClosureType.DEFAULT;
        if (input.getClosureType() != null && !input.getClosureType().isEmpty()) {
            try {
                closureType = Vote.ClosureType.valueOf(input.getClosureType());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("유효하지 않은 종료 타입입니다: " + input.getClosureType());
            }
        }

        // 종료 일자 계산
        LocalDate finishedAt = calculateFinishDate(closureType, input.getCustomDays());

        // Vote 엔티티 생성
        Vote.VoteBuilder voteBuilder = Vote.builder()
                .id(UUID.randomUUID())
                .question(input.getTitle())
                .category(input.getCategory())
                .status(Vote.Status.OPEN)
                .user(user)
                .closureType(closureType)
                .finishedAt(finishedAt);

        // 참여자 수 기준 종료 조건 설정 (PARTICIPANT_COUNT 타입일 때만)
        if (closureType == Vote.ClosureType.PARTICIPANT_COUNT) {
            voteBuilder.participantThreshold(input.getParticipantThreshold());
        }

        Vote vote = voteBuilder.build();

        // Vote 저장
        Vote savedVote = voteRepository.save(vote);

        // Option 생성 및 저장
        List<Option> savedOptions = new ArrayList<>();
        for (String optionContent : input.getOptions()) {
            Option option = Option.builder()
                    .id(UUID.randomUUID())
                    .vote(savedVote)
                    .content(optionContent)
                    .build();
            savedOptions.add(optionRepository.save(option));
        }

        // VotePayload 생성 및 반환
        return VotePayload.fromEntity(savedVote, savedOptions);
    }

    /**
     * 종료 조건의 유효성을 검증합니다.
     *
     * @param input 투표 생성 입력 데이터
     */
    private void validateClosureConditions(CreateVoteInput input) {
        if (input.getClosureType() == null || input.getClosureType().isEmpty()) {
            return; // 기본값 사용
        }

        Vote.ClosureType closureType;
        try {
            closureType = Vote.ClosureType.valueOf(input.getClosureType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 종료 타입입니다: " + input.getClosureType());
        }

        switch (closureType) {
            case CUSTOM_DAYS:
                if (input.getCustomDays() == null || input.getCustomDays() <= 0) {
                    throw new IllegalArgumentException("CUSTOM_DAYS 타입은 양수의 customDays 값이 필요합니다.");
                }
                if (input.getCustomDays() > 365) {
                    throw new IllegalArgumentException("customDays는 365일을 초과할 수 없습니다.");
                }
                break;
            case PARTICIPANT_COUNT:
                if (input.getParticipantThreshold() == null || input.getParticipantThreshold() <= 0) {
                    throw new IllegalArgumentException("PARTICIPANT_COUNT 타입은 양수의 participantThreshold 값이 필요합니다.");
                }
                if (input.getParticipantThreshold() > 10000) {
                    throw new IllegalArgumentException("participantThreshold는 10000명을 초과할 수 없습니다.");
                }
                break;
            case DEFAULT:
                // 기본 타입은 추가 검증 불필요
                break;
        }
    }

    /**
     * 종료 타입에 따라 종료 일자를 계산합니다.
     *
     * @param closureType 종료 타입
     * @param customDays 커스텀 일수 (CUSTOM_DAYS 타입일 때만 사용)
     * @return 계산된 종료 일자
     */
    private LocalDate calculateFinishDate(Vote.ClosureType closureType, Integer customDays) {
        LocalDate now = LocalDate.now();

        switch (closureType) {
            case DEFAULT:
                return now.plusDays(7); // 기본 7일
            case CUSTOM_DAYS:
                return now.plusDays(customDays != null ? customDays : 7);
            case PARTICIPANT_COUNT:
                // 참여자 수 기준은 날짜를 매우 미래로 설정 (실질적으로 무제한)
                return now.plusYears(10);
            default:
                return now.plusDays(7);
        }
    }
}