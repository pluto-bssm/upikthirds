package pluto.upik.shared.ai.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.shared.ai.data.DTO.GuideResponseDTO;
import pluto.upik.shared.ai.service.AIService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final VoteRepository voteRepository;

    /**
     * AI 가이드 생성을 위한 POST 요청을 처리합니다.
     *
     * @param vote_id      가이드 생성을 위한 투표의 UUID
     * @param vote_category 투표 카테고리
     * @return 생성된 가이드 정보를 담은 GuideResponseDTO 객체
     */
    @PostMapping("/AI")
    public GuideResponseDTO ai(@RequestBody UUID vote_id, String vote_category) {
        return aiService.generateAndSaveGuide(vote_id,vote_category);
    }
}
