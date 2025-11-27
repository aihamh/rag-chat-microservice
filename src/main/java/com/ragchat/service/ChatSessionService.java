package com.ragchat.service;

import com.ragchat.dto.*;
import com.ragchat.entity.ChatSession;
import com.ragchat.exception.ResourceNotFoundException;
import com.ragchat.repository.ChatMessageRepository;
import com.ragchat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        log.info("Creating new chat session for user: {}", request.getUserId());

        ChatSession session = ChatSession.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .isFavorite(false)
                .build();

        ChatSession savedSession = sessionRepository.save(session);
        log.info("Created chat session with ID: {}", savedSession.getId());

        return SessionResponse.fromEntityWithCount(savedSession, 0);
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(UUID sessionId) {
        log.debug("Fetching session with ID: {}", sessionId);

        ChatSession session = findSessionById(sessionId);
        int messageCount = (int) messageRepository.countBySessionId(sessionId);

        return SessionResponse.fromEntityWithCount(session, messageCount);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getSessionsByUser(String userId) {
        log.debug("Fetching sessions for user: {}", userId);

        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(session -> {
                    int messageCount = (int) messageRepository.countBySessionId(session.getId());
                    return SessionResponse.fromEntityWithCount(session, messageCount);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<SessionResponse> getSessionsByUserPaginated(String userId, int page, int size) {
        log.debug("Fetching paginated sessions for user: {} (page: {}, size: {})", userId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<ChatSession> sessionPage = sessionRepository.findByUserId(userId, pageable);

        List<SessionResponse> content = sessionPage.getContent().stream()
                .map(session -> {
                    int messageCount = (int) messageRepository.countBySessionId(session.getId());
                    return SessionResponse.fromEntityWithCount(session, messageCount);
                })
                .toList();

        return PageResponse.from(sessionPage, content);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getFavoriteSessions(String userId) {
        log.debug("Fetching favorite sessions for user: {}", userId);

        return sessionRepository.findByUserIdAndIsFavoriteTrue(userId)
                .stream()
                .map(session -> {
                    int messageCount = (int) messageRepository.countBySessionId(session.getId());
                    return SessionResponse.fromEntityWithCount(session, messageCount);
                })
                .toList();
    }

    @Transactional
    public SessionResponse updateSession(UUID sessionId, UpdateSessionRequest request) {
        log.info("Updating session with ID: {}", sessionId);

        ChatSession session = findSessionById(sessionId);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            session.setTitle(request.getTitle());
            log.debug("Updated session title to: {}", request.getTitle());
        }

        if (request.getIsFavorite() != null) {
            session.setIsFavorite(request.getIsFavorite());
            log.debug("Updated session favorite status to: {}", request.getIsFavorite());
        }

        ChatSession updatedSession = sessionRepository.save(session);
        int messageCount = (int) messageRepository.countBySessionId(sessionId);

        log.info("Session {} updated successfully", sessionId);
        return SessionResponse.fromEntityWithCount(updatedSession, messageCount);
    }

    @Transactional
    public SessionResponse renameSession(UUID sessionId, String newTitle) {
        log.info("Renaming session {} to: {}", sessionId, newTitle);

        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("New title cannot be empty");
        }

        ChatSession session = findSessionById(sessionId);
        session.setTitle(newTitle);

        ChatSession updatedSession = sessionRepository.save(session);
        int messageCount = (int) messageRepository.countBySessionId(sessionId);

        log.info("Session {} renamed successfully", sessionId);
        return SessionResponse.fromEntityWithCount(updatedSession, messageCount);
    }

    @Transactional
    public SessionResponse toggleFavorite(UUID sessionId) {
        log.info("Toggling favorite status for session: {}", sessionId);

        ChatSession session = findSessionById(sessionId);
        session.setIsFavorite(!session.getIsFavorite());

        ChatSession updatedSession = sessionRepository.save(session);
        int messageCount = (int) messageRepository.countBySessionId(sessionId);

        log.info("Session {} favorite status toggled to: {}", sessionId, updatedSession.getIsFavorite());
        return SessionResponse.fromEntityWithCount(updatedSession, messageCount);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        log.info("Deleting session with ID: {}", sessionId);

        ChatSession session = findSessionById(sessionId);
        sessionRepository.delete(session);

        log.info("Session {} and associated messages deleted successfully", sessionId);
    }

    private ChatSession findSessionById(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatSession", "id", sessionId));
    }
}
