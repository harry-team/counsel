package com.harry.counsel.java.config.security;

import com.harry.counsel.java.config.jwt.JwtAuthenticationFilter;
import com.harry.counsel.java.config.jwt.JwtUtil;
import com.harry.counsel.java.domain.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/ws/**", "/login.html").permitAll()
                        .requestMatchers("/api/v1/auth/me").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(successHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        log.info("successHandler start!!");
        return (request, response, authentication) -> {
            String email = authentication.getName();
            // UserDetails 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // userId 조회 (임시로 email 해시 사용, 실제로는 DB 조회 필요)
            // TODO: 실제 애플리케이션에서는 UserRepository를 통해 userId 조회
            Long userId = (long) email.hashCode(); // 임시 userId, 실제로는 DB에서 가져와야 함

            // JWT 토큰 생성
            String jwt = jwtUtil.generateToken(userDetails, userId);

            // 리다이렉트
            response.sendRedirect("/api/v1/auth/success?token=" + jwt);
        };
    }
}