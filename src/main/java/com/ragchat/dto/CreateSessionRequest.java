package com.ragchat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new chat session")
public class CreateSessionRequest {

    @NotBlank(message = "User ID is required")
    @Size(max = 255, message = "User ID must not exceed 255 characters")
    @Schema(description = "Unique identifier of the user", example = "user-123")
    private String userId;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    @Schema(description = "Title of the chat session", example = "New Chat Session")
    private String title;
}
