package com.example.bookstore.security;

import com.example.bookstore.security.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        log.info("=== OAuth2 Login Success ===");
        log.info("Email: {}", oAuth2User.getUser().getEmail());
        log.info("Name: {}", oAuth2User.getUser().getName());
        log.info("Role: {}", oAuth2User.getUser().getRole());

        // JWT 토큰 생성
        String email = oAuth2User.getUser().getEmail();
        String role = oAuth2User.getUser().getRole().name();

        String accessToken = jwtTokenProvider.createAccessToken(email, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        log.info("Access Token Generated");
        log.info("Refresh Token Generated");

        // 방법 1: HTML로 토큰 표시 (간단 테스트용)

        // UTF-8 인코딩 설정
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<html><head><title>OAuth2 Login Success</title></head><body>");
        out.println("<h1>✅ Google 로그인 성공!</h1>");
        out.println("<h3>사용자 정보:</h3>");
        out.println("<p>이메일: " + oAuth2User.getUser().getEmail() + "</p>");
        out.println("<p>이름: " + oAuth2User.getUser().getName() + "</p>");
        out.println("<p>역할: " + oAuth2User.getUser().getRole() + "</p>");
        out.println("<h3>발급된 토큰:</h3>");
        out.println("<p><strong>Access Token:</strong></p>");
        out.println("<textarea style='width:100%;height:100px'>" + accessToken + "</textarea>");
        out.println("<p><strong>Refresh Token:</strong></p>");
        out.println("<textarea style='width:100%;height:100px'>" + refreshToken + "</textarea>");
        out.println("<hr>");
        out.println("<p>Postman에서 이 토큰을 복사해서 사용하세요:</p>");
        out.println("<pre>Authorization: Bearer " + accessToken + "</pre>");
        out.println("<hr>");
        out.println("<a href='/api/users/me'>내 정보 보기 (토큰 필요)</a>");
        out.println("</body></html>");
        out.flush();
    }
}