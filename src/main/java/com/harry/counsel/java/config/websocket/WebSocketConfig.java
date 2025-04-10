package com.harry.counsel.java.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 * STOMP 프로토콜을 사용한 WebSocket 메시지 브로커 설정
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketChannelInterceptor channelInterceptor;

    @Value("${websocket.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${websocket.endpoint:/ws}")
    private String endpoint;

    @Value("${websocket.destination-prefixes:/app}")
    private String applicationDestinationPrefixes;

    @Value("${websocket.broker-prefixes:/topic,/queue}")
    private String brokerPrefixes;

    /**
     * STOMP 엔드포인트 등록
     * 클라이언트가 웹소켓 연결을 맺을 수 있는 엔드포인트 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering STOMP endpoints: {}", endpoint);

        // SockJS를 통한 엔드포인트 설정
        registry.addEndpoint(endpoint)
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS();
    }

    /**
     * 메시지 브로커 설정
     * 메시지 라우팅을 위한 브로커 및 접두사 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 구독 접두사 설정 (클라이언트 -> 서버)
        String[] prefixes = brokerPrefixes.split(",");
        registry.enableSimpleBroker(prefixes);
        log.info("Enabled simple broker with prefixes: {}", brokerPrefixes);

        // 메시지 발행 접두사 설정 (서버 -> 클라이언트)
        registry.setApplicationDestinationPrefixes(applicationDestinationPrefixes);
        log.info("Set application destination prefixes: {}", applicationDestinationPrefixes);

        // 특정 사용자에게 메시지를 보내기 위한 접두사
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 인바운드 채널 인터셉터 설정
     * 클라이언트로부터 받는 메시지를 처리하기 전에 인터셉트
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        log.info("Configuring client inbound channel with interceptor");
        registration.interceptors(channelInterceptor);
    }
}
