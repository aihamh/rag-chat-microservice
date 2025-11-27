package com.ragchat.dto;

import com.ragchat.entity.ChatSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Chat session response")
public class SessionResponse {

    @Schema(description = "Unique session identifier")
    private UUID id;

    @Schema(description = "User ID who owns this session")
    private String userId;

    @Schema(description = "Session title")
    private String title;

    @Schema(description = "Whether the session is marked as favorite")
    private Boolean isFavorite;

    @Schema(description = "Number of messages in this session")
    private Integer messageCount;

    @Schema(description = "Session creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Session last update timestamp")
    private LocalDateTime updatedAt;

    public static SessionResponse fromEntity(ChatSession session) {
        return SessionResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .title(session.getTitle())
                .isFavorite(session.getIsFavorite())
                .messageCount(session.getMessages() != null ? session.getMessages().size() : 0)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    public static SessionResponse fromEntityWithCount(ChatSession session, int messageCount) {
        return SessionResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .title(session.getTitle())
                .isFavorite(session.getIsFavorite())
                .messageCount(messageCount)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}
