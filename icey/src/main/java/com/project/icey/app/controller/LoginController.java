package com.project.icey.app.controller;

import com.project.icey.app.domain.User;
import com.project.icey.app.dto.LoginRequestDto;
import com.project.icey.app.repository.UserRepository;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.*;
import com.project.icey.global.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class LoginController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";


    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseTemplete<String>> logout(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 액세스 토큰 추출
        String accessToken = tokenService.extractAccessToken(request).orElse(null);

        if (accessToken == null) {
            return ResponseEntity.status(401).body(
                    ApiResponseTemplete.<String>builder()
                            .status(401)
                            .success(false)
                            .message("인증되지 않은 사용자입니다. (액세스 토큰 없음)")
                            .data(null)
                            .build()
            );
        }

        // 토큰 유효성 검사 (토큰이 만료되었어도 로그아웃은 가능해야 함)
        boolean isValid = tokenService.validateToken(accessToken);

        // 만료된 토큰에서도 로그아웃 처리 가능하도록 수정
        tokenService.extractEmail(accessToken).ifPresent(tokenService::removeRefreshToken);

        // 클라이언트 쿠키/헤더에서 토큰 제거
        response.setHeader("Authorization", "");
        response.setHeader("Refresh-Token", "");

        return ResponseEntity.ok(
                ApiResponseTemplete.<String>builder()
                        .status(200)
                        .success(true)
                        .message("로그아웃 성공")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/login/google")
    public RedirectView redirectToGoogle() {
        return new RedirectView("/oauth2/authorization/google");
    }

    @GetMapping("/login/kakao")
    public RedirectView redirectToKakao() {
        return new RedirectView("/oauth2/authorization/kakao");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseTemplete<Map<String, String>>> login(@RequestBody LoginRequestDto loginRequest) {
        // 이메일로 사용자 찾기
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        // 사용자가 존재하지 않으면 404 반환
        if (userOpt.isEmpty()) {
            return ApiResponseTemplete.error(ErrorCode.NOT_FOUND_USER_EXCEPTION, null);
        }

        User user = userOpt.get();

        // accessToken 생성
        String accessToken = tokenService.createAccessToken(user.getEmail());

        // 필요한 데이터만 포함해서 반환
        Map<String, String> responseData = new HashMap<>();
        responseData.put("email", user.getEmail());
        responseData.put("userId", String.valueOf(user.getId()));
        responseData.put("accessToken", accessToken);

        // 성공적인 응답 반환
        return ApiResponseTemplete.success(SuccessCode.LOGIN_USER_SUCCESS, responseData);
    }
}
