package pluto.upik.domain.IAM;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class IamRootQueryResolver {

    /**
     * IAM 쿼리의 루트 진입점
     *
     * @return IamQuery 객체
     */
    @QueryMapping
    public IamQuery iam() {
        return new IamQuery();
    }
}