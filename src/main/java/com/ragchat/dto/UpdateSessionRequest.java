package com.ragchat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a chat session")
public class UpdateSessionRequest {

    @Size(max = 500, message = "Title must not exceed 500 characters")
    @Schema(description = "New title for the chat session", example = "Updated Chat Title")
    private String title;

    @Schema(description = "Mark or unmark session as favorite", example = "true")
    private Boolean isFavorite;
}
