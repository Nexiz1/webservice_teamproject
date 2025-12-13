package com.example.bookstore.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    // IP별 요청 횟수 저장
    private final Map<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();

    // 제한 설정: 분당 100회
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long TIME_WINDOW_MS = 60_000; // 1분

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);

        // 헬스체크는 rate limit 제외
        if (httpRequest.getRequestURI().equals("/health")) {
            chain.doFilter(request, response);
            return;
        }

        RateLimitInfo rateLimitInfo = requestCounts.compute(clientIp, (key, info) -> {
            long now = System.currentTimeMillis();
            if (info == null || now - info.windowStart > TIME_WINDOW_MS) {
                return new RateLimitInfo(now, new AtomicInteger(1));
            }
            info.count.incrementAndGet();
            return info;
        });

        if (rateLimitInfo.count.get() > MAX_REQUESTS_PER_MINUTE) {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("""
                {
                    "timestamp": "%s",
                    "status": 429,
                    "code": "TOO_MANY_REQUESTS",
                    "message": "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.",
                    "path": "%s"
                }
                """.formatted(java.time.LocalDateTime.now(), httpRequest.getRequestURI()));
            return;
        }

        // Rate Limit 헤더 추가
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
        httpResponse.setHeader("X-RateLimit-Remaining",
                String.valueOf(Math.max(0, MAX_REQUESTS_PER_MINUTE - rateLimitInfo.count.get())));

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateLimitInfo {
        long windowStart;
        AtomicInteger count;

        RateLimitInfo(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}