package com.harry.counsel.java.config.websocket;

import com.harry.counsel.java.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 채널 인터셉터
 * 웹소켓 연결 및 메시지 송수신 시 JWT 토큰 검증을 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 메시지 전송 전 인터셉트
     * 주로 연결 요청(CONNECT) 시 JWT 토큰을 검증하여 사용자 인증
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            // CONNECT 명령일 때 JWT 토큰 검증
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                authenticateUser(accessor);
            }
            // SUBSCRIBE 명령일 때 구독 권한 검증
            else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                validateSubscription(accessor);
            }
        }

        return message;
    }

    /**
     * JWT 토큰을 통한 사용자 인증
     */
    private void authenticateUser(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");
        log.debug("WebSocket Connection - Authorization token: {}", token);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                // JWT 토큰 검증
                if (jwtUtil.validateToken(token)) {
                    // 토큰에서 사용자 정보 추출
                    UserDetails userDetails = jwtUtil.getUserDetailsFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);

                    // 인증 객체 생성 및 설정
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);

                    // 사용자 정보를 세션 속성에 저장
                    Map<String, Object> sessionAttributes = new HashMap<>();
                    sessionAttributes.put("userId", userId);
                    sessionAttributes.put("username", userDetails.getUsername());
                    accessor.setSessionAttributes(sessionAttributes);

                    log.debug("Authentication successful for WebSocket connection. User: {}", userDetails.getUsername());
                } else {
                    log.warn("Invalid JWT token for WebSocket connection");
                }
            } catch (Exception e) {
                log.error("JWT authentication failed: {}", e.getMessage());
            }
        } else {
            log.warn("No Authorization header found or invalid format");
        }
    }

    /**
     * 구독 요청에 대한 권한 검증
     * 특정 채팅방에 대한 구독 요청 시 해당 채팅방에 접근 권한이 있는지 확인
     */
    private void validateSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/topic/chat/room/")) {
            // 채팅방 ID 추출
            String roomId = destination.substring("/topic/chat/room/".length());

            // 사용자 정보 추출
            Object userIdObj = accessor.getSessionAttributes().get("userId");

            if (userIdObj != null) {
                Long userId = (Long) userIdObj;
                log.debug("User {} subscribing to chat room: {}", userId, roomId);

                // TODO: 채팅방 접근 권한 검증 로직 추가
                // 예: chatService.canAccessRoom(userId, roomId)
            } else {
                log.warn("Unauthenticated user trying to subscribe to: {}", destination);
                // 필요에 따라 예외 발생 또는 구독 거부 로직 구현 가능
            }
        }
    }
}
