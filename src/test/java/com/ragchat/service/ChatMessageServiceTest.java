package com.ragchat.service;

import com.ragchat.dto.CreateMessageRequest;
import com.ragchat.dto.MessageResponse;
import com.ragchat.entity.ChatMessage;
import com.ragchat.entity.ChatSession;
import com.ragchat.exception.ResourceNotFoundException;
import com.ragchat.repository.ChatMessageRepository;
import com.ragchat.repository.ChatSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private ChatSessionRepository sessionRepository;

    @InjectMocks
    private ChatMessageService messageService;

    private UUID sessionId;
    private UUID messageId;
    private ChatSession testSession;
    private ChatMessage testMessage;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        testSession = ChatSession.builder()
                .id(sessionId)
                .userId("user-123")
                .title("Test Session")
                .isFavorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testMessage = ChatMessage.builder()
                .id(messageId)
                .session(testSession)
                .sender(ChatMessage.SenderType.USER)
                .content("Hello, world!")
                .context("Some RAG context")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add a message to session successfully")
    void addMessage_Success() {
        CreateMessageRequest request = CreateMessageRequest.builder()
                .sender(ChatMessage.SenderType.USER)
                .content("Hello!")
                .context("Context")
                .build();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(testMessage);

        MessageResponse response = messageService.addMessage(sessionId, request);

        assertThat(response).isNotNull();
        assertThat(response.getSender()).isEqualTo(ChatMessage.SenderType.USER);
        assertThat(response.getContent()).isEqualTo("Hello, world!");

        verify(messageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Should throw exception when adding message to non-existent session")
    void addMessage_SessionNotFound() {
        CreateMessageRequest request = CreateMessageRequest.builder()
                .sender(ChatMessage.SenderType.USER)
                .content("Hello!")
                .build();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.addMessage(sessionId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ChatSession not found");
    }

    @Test
    @DisplayName("Should get all messages for a session")
    void getMessages_Success() {
        List<ChatMessage> messages = List.of(testMessage);

        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)).thenReturn(messages);

        List<MessageResponse> responses = messageService.getMessages(sessionId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getContent()).isEqualTo("Hello, world!");
    }

    @Test
    @DisplayName("Should throw exception when getting messages for non-existent session")
    void getMessages_SessionNotFound() {
        when(sessionRepository.existsById(sessionId)).thenReturn(false);

        assertThatThrownBy(() -> messageService.getMessages(sessionId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get message by ID successfully")
    void getMessage_Success() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(testMessage));

        MessageResponse response = messageService.getMessage(messageId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(messageId);
        assertThat(response.getContent()).isEqualTo("Hello, world!");
    }

    @Test
    @DisplayName("Should throw exception when message not found")
    void getMessage_NotFound() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.getMessage(messageId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ChatMessage not found");
    }

    @Test
    @DisplayName("Should delete message successfully")
    void deleteMessage_Success() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(testMessage));

        messageService.deleteMessage(messageId);

        verify(messageRepository, times(1)).delete(testMessage);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent message")
    void deleteMessage_NotFound() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.deleteMessage(messageId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get message count for session")
    void getMessageCount_Success() {
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageRepository.countBySessionId(sessionId)).thenReturn(10L);

        long count = messageService.getMessageCount(sessionId);

        assertThat(count).isEqualTo(10L);
    }
}
