package com.harry.counsel.java.domain.counsel.service;


import com.harry.counsel.java.domain.counsel.entity.ChatMessage;
import com.harry.counsel.java.domain.counsel.entity.ChatRoom;
import com.harry.counsel.java.domain.counsel.repository.ChatMessageRepository;
import com.harry.counsel.java.domain.counsel.repository.ChatRoomRepository;
import com.harry.counsel.java.domain.user.entity.User;
import com.harry.counsel.java.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom createRoom(Long userId, Long counselorId) {
        log.info("Creating room for userId: {}, counselorId: {}", userId, counselorId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User counselor = userRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("Counselor not found: " + counselorId));
        ChatRoom room = new ChatRoom(user, counselor);
        return chatRoomRepository.save(room);
    }

    public boolean canAccessRoom(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        return room.getUser().getId().equals(userId) || room.getCounselor().getId().equals(userId);
    }

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));

        if (!canAccessRoom(senderId, roomId)) {
            throw new SecurityException("User does not have access to this room");
        }

        ChatMessage message = new ChatMessage(room, sender, content);
        return chatMessageRepository.save(message);
    }

    public Page<ChatMessage> getMessages(Long roomId, Long userId, Pageable pageable) {
        if (!canAccessRoom(userId, roomId)) {
            throw new SecurityException("User does not have access to this room");
        }
        return chatMessageRepository.findByRoomIdOrderBySentAtDesc(roomId, pageable);
    }
}
