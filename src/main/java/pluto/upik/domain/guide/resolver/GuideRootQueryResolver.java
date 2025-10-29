package pluto.upik.domain.guide.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideQuery;

/**
 * 가이드 관련 GraphQL 루트 쿼리 리졸버
 * 가이드 쿼리에 대한 루트 진입점을 제공합니다.
 */
@Controller
@Slf4j
public class GuideRootQueryResolver {

    /**
     * 가이드 쿼리 루트 진입점
     *
     * @return 가이드 쿼리 객체
     */
    @QueryMapping
    public GuideQuery guide() {
        log.info("GraphQL 가이드 쿼리 루트 진입점 호출");
        return new GuideQuery();
    }
}