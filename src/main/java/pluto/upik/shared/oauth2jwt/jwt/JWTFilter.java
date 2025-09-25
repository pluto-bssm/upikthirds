package pluto.upik.shared.oauth2jwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pluto.upik.shared.oauth2jwt.dto.CustomOAuth2User;
import pluto.upik.shared.oauth2jwt.dto.UserDTO;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // JWT 검증을 스킵할 경로 접두사 목록
    private static final Set<String> SKIP_PATH_PREFIXES = Set.of(
            "/oauth2/", "/login/", "/static/", "/css/", "/js/", "/images/"
    );

    // JWT 검증을 스킵할 정확한 경로 목록
    private static final Set<String> SKIP_EXACT_PATHS = Set.of(
            "/auth/reissue", "/favicon.ico", "/error", "/api/my"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 1. 요청 경로가 필터링을 스킵해야 하는지 확인
        if (shouldSkipFilter(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Access Token 추출 (헤더 우선, 쿠키 보조)
        String accessToken = extractAccessToken(request);
        System.out.println("accessToken = " + accessToken);
        if (accessToken == null) {
            log.debug("No Access Token found for URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Access Token 처리
        try {
            if (jwtUtil.isExpired(accessToken)) {
                log.debug("Access Token expired for URI: {}. Attempting refresh.", requestURI);
                handleExpiredAccessToken(request, response);
            } else {
                validateAndSetAuthentication(accessToken);
            }
        } catch (Exception e) {
            log.warn("Error processing JWT for URI: {}. Error: {}", requestURI, e.getMessage());
        }
        setAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 URI가 필터링을 건너뛰어야 하는지 확인합니다.
     */
    private boolean shouldSkipFilter(String requestURI) {
        return SKIP_EXACT_PATHS.contains(requestURI) ||
                SKIP_PATH_PREFIXES.stream().anyMatch(requestURI::startsWith);
    }

    /**
     * 요청에서 Access Token을 추출합니다 (헤더 우선, 쿠키 보조).
     */
    private String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return extractCookieValue(request, "Authorization").orElse(null);
    }

    /**
     * 만료된 Access Token을 처리하고, 유효한 Refresh Token이 있다면 새로운 Access Token을 발급합니다.
     */
    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        extractCookieValue(request, "refreshToken")
                .filter(this::isValidRefreshToken)
                .ifPresent(refreshToken -> {
                    try {
                        String newAccessToken = createNewAccessToken(refreshToken);
                        setAuthentication(newAccessToken);
                        setAccessTokenCookie(response, newAccessToken);
                        log.info("Access Token refreshed successfully for user: {}", jwtUtil.getUsername(refreshToken));
                    } catch (Exception e) {
                        log.warn("Failed to refresh Access Token: {}", e.getMessage());
                    }
                });
    }

    /**
     * Refresh Token의 유효성을 검증합니다.
     */
    private boolean isValidRefreshToken(String token) {
        try {
            return "refresh".equals(jwtUtil.getCategory(token)) &&
                    !jwtUtil.isExpired(token) &&
                    refreshTokenRepository.existsByToken(token);
        } catch (Exception e) {
            log.warn("Invalid refresh token provided: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 새로운 Access Token을 생성합니다.
     */
    private String createNewAccessToken(String refreshToken) {
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        return jwtUtil.createJwt("access", username, role, 900000L); // 15분
    }

    /**
     * 새로운 Access Token을 쿠키에 설정합니다.
     */
    private void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = ResponseCookie.from("Authorization", accessToken)
                .path("/")
                .maxAge(15 * 60) // 15분
                .httpOnly(false) // JavaScript 접근 허용
                .secure(false)   // TODO: 프로덕션에서는 true로 설정
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 주어진 Access Token을 검증하고 SecurityContext에 인증 정보를 설정합니다.
     */
    private void validateAndSetAuthentication(String accessToken) {
        if (isValidAccessToken(accessToken)) {
            setAuthentication(accessToken);
        }
    }

    /**
     * Access Token의 유효성을 검증합니다.
     */
    private boolean isValidAccessToken(String token) {
        try {
            return "access".equals(jwtUtil.getCategory(token)) && !jwtUtil.isExpired(token);
        } catch (Exception e) {
            log.warn("Invalid access token provided: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 정보를 기반으로 SecurityContext에 인증 정보를 설정합니다.
     */
    private void setAuthentication(String token) {
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        if (username == null || role == null || "ROLE_DELETED".equals(role)) {
            log.warn("Authentication failed for token. Username: {}, Role: {}", username, role);
            return;
        }

        String name = userRepository.findNameByUsername(username)
                .filter(n -> !n.trim().isEmpty())
                .orElse(username);

        UserDTO userDTO = UserDTO.builder()
                .username(username)
                .name(name)
                .role(role)
                .build();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO, userRepository);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customOAuth2User, null, customOAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Authentication set for user: {}", username);
    }

    /**
     * 요청의 쿠키에서 특정 이름의 값을 추출합니다.
     */
    private Optional<String> extractCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}