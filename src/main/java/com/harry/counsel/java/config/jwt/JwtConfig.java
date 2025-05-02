package com.harry.counsel.java.config.jwt;

import com.harry.counsel.java.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token-validity-seconds}")
    private long tokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTokenValidityInSeconds;

    @Bean
    public JwtUtil jwtUtil(UserDetailsService userDetailsService, UserRepository userRepository) {
        return new JwtUtil(secret, tokenValidityInSeconds, refreshTokenValidityInSeconds, userDetailsService, userRepository);
    }
}
