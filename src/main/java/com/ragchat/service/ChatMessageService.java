package com.ragchat.service;

import com.ragchat.dto.CreateMessageRequest;
import com.ragchat.dto.MessageResponse;
import com.ragchat.dto.PageResponse;
import com.ragchat.entity.ChatMessage;
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
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;

    @Transactional
    public MessageResponse addMessage(UUID sessionId, CreateMessageRequest request) {
        log.info("Adding message to session: {} from sender: {}", sessionId, request.getSender());

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatSession", "id", sessionId));

        ChatMessage message = ChatMessage.builder()
                .session(session)
                .sender(request.getSender())
                .content(request.getContent())
                .context(request.getContext())
                .build();

        ChatMessage savedMessage = messageRepository.save(message);
        log.info("Message {} added to session {}", savedMessage.getId(), sessionId);

        return MessageResponse.fromEntity(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID sessionId) {
        log.debug("Fetching all messages for session: {}", sessionId);

        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("ChatSession", "id", sessionId);
        }

        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(MessageResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> getMessagesPaginated(UUID sessionId, int page, int size) {
        log.debug("Fetching paginated messages for session: {} (page: {}, size: {})", sessionId, page, size);

        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("ChatSession", "id", sessionId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<ChatMessage> messagePage = messageRepository.findBySessionId(sessionId, pageable);

        List<MessageResponse> content = messagePage.getContent().stream()
                .map(MessageResponse::fromEntity)
                .toList();

        return PageResponse.from(messagePage, content);
    }

    @Transactional(readOnly = true)
    public MessageResponse getMessage(UUID messageId) {
        log.debug("Fetching message with ID: {}", messageId);

        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId));

        return MessageResponse.fromEntity(message);
    }

    @Transactional
    public void deleteMessage(UUID messageId) {
        log.info("Deleting message with ID: {}", messageId);

        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId));

        messageRepository.delete(message);
        log.info("Message {} deleted successfully", messageId);
    }

    @Transactional(readOnly = true)
    public long getMessageCount(UUID sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("ChatSession", "id", sessionId);
        }
        return messageRepository.countBySessionId(sessionId);
    }
}
