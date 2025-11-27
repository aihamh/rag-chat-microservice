package com.ragchat.service;

import com.ragchat.dto.CreateSessionRequest;
import com.ragchat.dto.SessionResponse;
import com.ragchat.dto.UpdateSessionRequest;
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
class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository messageRepository;

    @InjectMocks
    private ChatSessionService sessionService;

    private UUID sessionId;
    private ChatSession testSession;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        testSession = ChatSession.builder()
                .id(sessionId)
                .userId("user-123")
                .title("Test Session")
                .isFavorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create a new session successfully")
    void createSession_Success() {
        CreateSessionRequest request = new CreateSessionRequest("user-123", "New Session");

        when(sessionRepository.save(any(ChatSession.class))).thenReturn(testSession);

        SessionResponse response = sessionService.createSession(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("user-123");
        assertThat(response.getTitle()).isEqualTo("Test Session");
        assertThat(response.getIsFavorite()).isFalse();

        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("Should get session by ID successfully")
    void getSession_Success() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(messageRepository.countBySessionId(sessionId)).thenReturn(5L);

        SessionResponse response = sessionService.getSession(sessionId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(sessionId);
        assertThat(response.getMessageCount()).isEqualTo(5);

        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    @DisplayName("Should throw exception when session not found")
    void getSession_NotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSession(sessionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ChatSession not found");
    }

    @Test
    @DisplayName("Should get all sessions for a user")
    void getSessionsByUser_Success() {
        List<ChatSession> sessions = List.of(testSession);

        when(sessionRepository.findByUserIdOrderByUpdatedAtDesc("user-123")).thenReturn(sessions);
        when(messageRepository.countBySessionId(any())).thenReturn(3L);

        List<SessionResponse> responses = sessionService.getSessionsByUser("user-123");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo("user-123");
    }

    @Test
    @DisplayName("Should update session title and favorite status")
    void updateSession_Success() {
        UpdateSessionRequest request = UpdateSessionRequest.builder()
                .title("Updated Title")
                .isFavorite(true)
                .build();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(testSession);
        when(messageRepository.countBySessionId(sessionId)).thenReturn(0L);

        SessionResponse response = sessionService.updateSession(sessionId, request);

        assertThat(response).isNotNull();
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("Should rename session successfully")
    void renameSession_Success() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(testSession);
        when(messageRepository.countBySessionId(sessionId)).thenReturn(0L);

        SessionResponse response = sessionService.renameSession(sessionId, "New Title");

        assertThat(response).isNotNull();
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("Should throw exception when renaming with empty title")
    void renameSession_EmptyTitle() {
        assertThatThrownBy(() -> sessionService.renameSession(sessionId, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title cannot be empty");
    }

    @Test
    @DisplayName("Should toggle favorite status")
    void toggleFavorite_Success() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(testSession);
        when(messageRepository.countBySessionId(sessionId)).thenReturn(0L);

        SessionResponse response = sessionService.toggleFavorite(sessionId);

        assertThat(response).isNotNull();
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    @DisplayName("Should delete session successfully")
    void deleteSession_Success() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        sessionService.deleteSession(sessionId);

        verify(sessionRepository, times(1)).delete(testSession);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent session")
    void deleteSession_NotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.deleteSession(sessionId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
