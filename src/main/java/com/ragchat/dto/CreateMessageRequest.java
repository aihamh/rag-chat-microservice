package com.ragchat.dto;

import com.ragchat.entity.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new chat message")
public class CreateMessageRequest {

    @NotNull(message = "Sender is required")
    @Schema(description = "Message sender type", example = "USER")
    private ChatMessage.SenderType sender;

    @NotBlank(message = "Content is required")
    @Schema(description = "Message content", example = "Hello, how can I help you today?")
    private String content;

    @Schema(description = "Optional RAG context retrieved for this message", example = "Retrieved document context...")
    private String context;
}
