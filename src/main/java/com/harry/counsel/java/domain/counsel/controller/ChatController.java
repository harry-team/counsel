package com.harry.counsel.java.domain.counsel.controller;

import com.harry.counsel.java.config.jwt.JwtUtil;
import com.harry.counsel.java.domain.counsel.dto.ChatMessageDto;
import com.harry.counsel.java.domain.counsel.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;

    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto message,
                            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received message for roomId: {}, sender: {}", roomId, userDetails.getUsername());

    }
}
