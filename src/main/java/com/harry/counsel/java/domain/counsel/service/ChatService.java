package com.harry.counsel.java.domain.counsel.service;


import com.harry.counsel.java.domain.counsel.entitiy.ChatRoom;
import com.harry.counsel.java.domain.counsel.repository.ChatRoomRepository;
import com.harry.counsel.java.domain.user.entity.User;
import com.harry.counsel.java.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom createRoom(Long userId, Long counselorId) {
        log.info("Creating room for userId: {}, counselorId: {}", userId, counselorId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User counselor = userRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("Counselor not found: " + counselorId));


        ChatRoom room = new ChatRoom(user, counselor);
        return roomRepository.save(room);
    }

    public boolean canAccessRoom(Long userId, Long roomId) {
        ChatRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        return room.getUser().getId().equals(userId) || room.getCounselor().getId().equals(userId);
    }
}
