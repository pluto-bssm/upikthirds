package pluto.upik.shared.oauth2jwt.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pluto.upik.shared.oauth2jwt.entity.RefreshTokenCode;
import pluto.upik.shared.oauth2jwt.repository.AccessTokenCodeRepository;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenCodeRepository;
import pluto.upik.shared.oauth2jwt.repository.RefreshTokenRepository;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ReCodeController {
    private final AccessTokenCodeRepository accessTokenCodeRepository;
    private final RefreshTokenCodeRepository refreshTokenCodeRepository;
    @GetMapping("/code")
    @Transactional
    public ResponseEntity<?> getCode(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam("code") String code
    ) {

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshTokenCodeRepository.findByCode(code).getRefreshToken())
                .maxAge(60 * 60 * 24 * 7) // 7일
                .secure(true) // 프로덕션에서는 true로 설정
                .path("/")
                .httpOnly(false)
                .sameSite("None") // 또는 필요에 따라 "None"으로 설정
                // .domain("yourdomain.com") // 필요한 경우 도메인 설정
                .build();

        response.addHeader("Authorization", accessTokenCodeRepository.findByCode(code).getAccessToken());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        accessTokenCodeRepository.deleteByCode(code);
        refreshTokenCodeRepository.deleteByCode(code);

        return ResponseEntity.ok(Map.of(
                "message", "Login Success",
                "code", "LOGIN_SUCCESS"
        ));
    }
}