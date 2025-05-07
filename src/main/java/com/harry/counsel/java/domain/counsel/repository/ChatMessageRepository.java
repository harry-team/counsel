package com.harry.counsel.java.domain.counsel.repository;

import com.harry.counsel.java.domain.counsel.entity.ChatMessage;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByRoomIdOrderBySentAtDesc(Long roomId, Pageable pageable);
}
