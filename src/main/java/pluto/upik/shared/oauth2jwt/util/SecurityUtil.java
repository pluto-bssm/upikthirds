package pluto.upik.shared.oauth2jwt.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.util.UUID;

/**
 * 보안 관련 유틸리티 클래스
 * 현재 인증된 사용자 정보를 쉽게 가져올 수 있는 메서드 제공
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자의 CustomOAuth2User 객체를 반환합니다.
     * 인증되지 않은 경우 예외를 발생시킵니다.
     * 
     * @return 현재 인증된 사용자의 CustomOAuth2User 객체
     * @throws BusinessException 인증되지 않은 경우
     */
    public CustomOAuth2User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BusinessException("인증되지 않은 사용자입니다.", "UNAUTHORIZED");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomOAuth2User)) {
            throw new BusinessException("인증 정보가 올바르지 않습니다.", "UNAUTHORIZED");
        }

        return (CustomOAuth2User) principal;
    }
    
    /**
     * 현재 인증된 사용자의 User 엔티티를 반환합니다.
     * 인증되지 않았거나 사용자 정보를 찾을 수 없는 경우 예외를 발생시킵니다.
     * 
     * @return 현재 인증된 사용자의 User 엔티티
     * @throws BusinessException 인증되지 않았거나 사용자 정보를 찾을 수 없는 경우
     */
    public User getCurrentUserEntity() {
        CustomOAuth2User oAuth2User = getCurrentUser();
        String username = oAuth2User.getUsername();
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("사용자 정보를 찾을 수 없습니다.", "USER_NOT_FOUND"));
    }
    
    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     * 인증되지 않았거나 사용자 정보를 찾을 수 없는 경우 예외를 발생시킵니다.
     * 
     * @return 현재 인증된 사용자의 UUID
     * @throws BusinessException 인증되지 않았거나 사용자 정보를 찾을 수 없는 경우
     */
    public UUID getCurrentUserId() {
        return getCurrentUserEntity().getId();
    }
    
    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     * 
     * @return 인증된 경우 true, 그렇지 않은 경우 false
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
}
