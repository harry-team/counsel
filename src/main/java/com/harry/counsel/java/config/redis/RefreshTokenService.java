package com.harry.counsel.java.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 리프레시 토큰 관리 서비스
 * Redis를 사용해 리프레시 토큰을 저장, 조회, 삭제
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * 리프레시 토큰 저장
     * @param userId 사용자 ID
     * @param refreshToken 리프레시 토큰
     * @param expirationSeconds 만료 시간 (초 단위)
     */
    public void saveRefreshToken(Long userId, String refreshToken, long expirationSeconds) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, expirationSeconds, TimeUnit.SECONDS);
    }

    /**
     * 리프레시 토큰 조회
     * @param userId 사용자 ID
     * @return 리프레시 토큰 (없으면 null)
     */
    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 리프레시 토큰 삭제 (로그아웃 시 사용)
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * 리프레시 토큰 유효성 검증
     * @param userId 사용자 ID
     * @param refreshToken 클라이언트가 제공한 리프레시 토큰
     * @return 유효 여부
     */
    public boolean isValidRefreshToken(Long userId, String refreshToken) {
        String storedToken = getRefreshToken(userId);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}
