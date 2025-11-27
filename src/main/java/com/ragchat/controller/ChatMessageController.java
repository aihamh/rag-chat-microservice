package com.ragchat.controller;

import com.ragchat.dto.*;
import com.ragchat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Chat Messages", description = "APIs for managing chat messages within sessions")
@SecurityRequirement(name = "apiKey")
public class ChatMessageController {

    private final ChatMessageService messageService;

    @PostMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Add a message to a session", description = "Adds a new message to an existing chat session")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Message added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<MessageResponse>> addMessage(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @Valid @RequestBody CreateMessageRequest request) {
        MessageResponse message = messageService.addMessage(sessionId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message added successfully", message));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Get all messages in a session", description = "Retrieves all messages in a chat session")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        List<MessageResponse> messages = messageService.getMessages(sessionId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/sessions/{sessionId}/messages/paginated")
    @Operation(summary = "Get paginated messages in a session", description = "Retrieves paginated messages in a chat session")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getMessagesPaginated(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        PageResponse<MessageResponse> messages = messageService.getMessagesPaginated(sessionId, page, size);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/messages/{messageId}")
    @Operation(summary = "Get a message by ID", description = "Retrieves a specific message by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<MessageResponse>> getMessage(
            @Parameter(description = "Message ID") @PathVariable UUID messageId) {
        MessageResponse message = messageService.getMessage(messageId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @DeleteMapping("/messages/{messageId}")
    @Operation(summary = "Delete a message", description = "Deletes a specific message by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @Parameter(description = "Message ID") @PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok(ApiResponse.success("Message deleted successfully", null));
    }

    @GetMapping("/sessions/{sessionId}/messages/count")
    @Operation(summary = "Get message count", description = "Returns the total number of messages in a session")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Long>> getMessageCount(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        long count = messageService.getMessageCount(sessionId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
