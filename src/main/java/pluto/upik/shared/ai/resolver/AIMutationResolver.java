package pluto.upik.shared.ai.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.shared.ai.data.DTO.GuideResponseDTO;
import pluto.upik.shared.ai.service.AIService;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AIMutationResolver {

    private final AIService aiService;

    /**
     * 주어진 투표 ID와 카테고리를 기반으로 AI 가이드를 생성하고 저장한 후, 결과를 반환합니다.
     *
     * @param voteId        가이드 생성을 위한 투표의 UUID
     * @param voteCategory  가이드 생성을 위한 투표 카테고리
     * @return              생성된 AI 가이드의 정보를 담은 GuideResponseDTO
     */
    @MutationMapping
    public GuideResponseDTO generateAIGuide(@Argument UUID voteId, @Argument String voteCategory) {
        return aiService.generateAndSaveGuide(voteId, voteCategory);
    }
}