package com.ragchat.dto;

import com.ragchat.entity.ChatMessage;
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
@Schema(description = "Chat message response")
public class MessageResponse {

    @Schema(description = "Unique message identifier")
    private UUID id;

    @Schema(description = "Session ID this message belongs to")
    private UUID sessionId;

    @Schema(description = "Message sender type")
    private ChatMessage.SenderType sender;

    @Schema(description = "Message content")
    private String content;

    @Schema(description = "Optional RAG context")
    private String context;

    @Schema(description = "Message creation timestamp")
    private LocalDateTime createdAt;

    public static MessageResponse fromEntity(ChatMessage message) {
        return MessageResponse.builder()
                .id(message.getId())
                .sessionId(message.getSession().getId())
                .sender(message.getSender())
                .content(message.getContent())
                .context(message.getContext())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
