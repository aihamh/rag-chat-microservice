package com.ragchat.repository;

import com.ragchat.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);

    Page<ChatSession> findByUserId(String userId, Pageable pageable);

    List<ChatSession> findByUserIdAndIsFavoriteTrue(String userId);

    Page<ChatSession> findByUserIdAndIsFavoriteTrue(String userId, Pageable pageable);

    long countByUserId(String userId);
}
