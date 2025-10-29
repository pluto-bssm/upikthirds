package pluto.upik.shared.filter.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.shared.filter.BadWordFilterService;
import pluto.upik.shared.filter.dto.CheckBadWordResponse;

/**
 * 욕설 검사 GraphQL Query Resolver
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class BadWordQueryResolver {

    private final BadWordFilterService badWordFilterService;

    /**
     * 텍스트에 욕설이 포함되어 있는지 검사합니다.
     *
     * @param text 검사할 텍스트
     * @return 욕설 검사 결과
     */
    @QueryMapping
    public CheckBadWordResponse checkBadWord(@Argument String text) {
        log.debug("욕설 검사 요청: text={}", text);

        if (text == null || text.trim().isEmpty()) {
            return CheckBadWordResponse.builder()
                    .containsBadWord(false)
                    .message("검사할 텍스트가 없습니다.")
                    .checkedText(text)
                    .build();
        }

        boolean containsBadWord = badWordFilterService.containsBadWord(text);

        if (containsBadWord) {
            log.warn("욕설 감지: text={}", text);
            return CheckBadWordResponse.detected(text);
        } else {
            log.debug("욕설 미감지: text={}", text);
            return CheckBadWordResponse.clean(text);
        }
    }
}
