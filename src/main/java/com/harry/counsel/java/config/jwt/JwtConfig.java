package com.harry.counsel.java.config.jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * JWT 관련 설정 클래스
 * JWT 토큰 생성 및 검증에 필요한 빈들을 설정
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token-validity-seconds:3600}")
    private long tokenValiditySeconds;

    @Value("${jwt.refresh-token-validity-seconds:86400}")
    private long refreshTokenValiditySeconds;

    /**
     * 비밀번호 인코더 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 매니저 빈 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * JWT 유틸리티 빈 등록
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(
                jwtSecret,
                tokenValiditySeconds,
                refreshTokenValiditySeconds,
                userDetailsService);
    }
}
