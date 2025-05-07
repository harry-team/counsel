package com.harry.counsel.java.domain.counsel.entity;

import com.harry.counsel.java.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    private String content;

    private LocalDateTime sentAt;

    public ChatMessage(ChatRoom room, User sender, String content) {
        this.room = room;
        this.sender = sender;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }
}
