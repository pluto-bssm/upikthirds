package pluto.upik.domain.IAM;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.shared.oauth2jwt.dto.IamDTO;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class IamQueryResolver {

    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자의 정보를 반환합니다.
     *
     * @param iamQuery IamQuery 객체
     * @return 현재 사용자 정보
     */
    @SchemaMapping(typeName = "IamQuery")
    public IamDTO getCurrentUser(IamQuery iamQuery) {
        UUID userId = securityUtil.getCurrentUserId();
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return IamDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        }

        return null; // 또는 기본값을 가진 DTO 반환
    }
}