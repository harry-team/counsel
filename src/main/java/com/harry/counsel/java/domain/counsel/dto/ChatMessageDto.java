package com.harry.counsel.java.domain.counsel.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageDto {

    private Long roomId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
}
