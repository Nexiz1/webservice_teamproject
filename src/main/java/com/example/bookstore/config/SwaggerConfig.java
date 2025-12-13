package com.example.bookstore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("온라인 서점 API")
                        .description("온라인 서점 백엔드 API 문서입니다.\n\n" +
                                "## 인증 방식\n" +
                                "- JWT 기반 인증\n" +
                                "- Access Token을 Authorization 헤더에 Bearer 토큰으로 전달\n\n" +
                                "## 역할\n" +
                                "- ROLE_USER: 일반 사용자\n" +
                                "- ROLE_ADMIN: 관리자\n\n" +
                                "## 테스트 계정\n" +
                                "- 일반 사용자: user@example.com / password123\n" +
                                "- 관리자: admin@example.com / admin123")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bookstore API Support")
                                .email("support@bookstore.com")))
                .servers(List.of(
                        new Server().url("http://113.198.66.75:10168").description("Production Server"),
                        new Server().url("http://localhost:" + serverPort).description("Local Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요")));
    }
}
