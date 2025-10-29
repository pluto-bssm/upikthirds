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

        // 현재는 테스트를 위해 첫 번째 사용자를 가져옴 (실제로는 인증된 사용자를 사용해야 함)
        User user = userRepository.findById(userId).orElseThrow();

        // Vote 엔티티 생성
        Vote vote = Vote.builder()
                .id(UUID.randomUUID())
                .question(input.getTitle())
                .category(input.getCategory())
                .status(Vote.Status.OPEN)
                .user(user)
                .finishedAt(LocalDate.now().plusDays(7)) // 기본 7일 후 종료
                .build();
        
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
}