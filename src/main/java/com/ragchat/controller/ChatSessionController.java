package com.ragchat.controller;

import com.ragchat.dto.*;
import com.ragchat.service.ChatSessionService;
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
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Chat Sessions", description = "APIs for managing chat sessions")
@SecurityRequirement(name = "apiKey")
public class ChatSessionController {

    private final ChatSessionService sessionService;

    @PostMapping
    @Operation(summary = "Create a new chat session", description = "Creates a new chat session for a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Session created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @Valid @RequestBody CreateSessionRequest request) {
        SessionResponse session = sessionService.createSession(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Session created successfully", session));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get a session by ID", description = "Retrieves a specific chat session by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<SessionResponse>> getSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        SessionResponse session = sessionService.getSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all sessions for a user", description = "Retrieves all chat sessions for a specific user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessionsByUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        List<SessionResponse> sessions = sessionService.getSessionsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Get paginated sessions for a user", description = "Retrieves paginated chat sessions for a specific user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<PageResponse<SessionResponse>>> getSessionsByUserPaginated(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        PageResponse<SessionResponse> sessions = sessionService.getSessionsByUserPaginated(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/user/{userId}/favorites")
    @Operation(summary = "Get favorite sessions for a user", description = "Retrieves all favorite chat sessions for a specific user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Favorite sessions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getFavoriteSessions(
            @Parameter(description = "User ID") @PathVariable String userId) {
        List<SessionResponse> sessions = sessionService.getFavoriteSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @PatchMapping("/{sessionId}")
    @Operation(summary = "Update a session", description = "Updates a chat session's title and/or favorite status")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<SessionResponse>> updateSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @Valid @RequestBody UpdateSessionRequest request) {
        SessionResponse session = sessionService.updateSession(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("Session updated successfully", session));
    }

    @PatchMapping("/{sessionId}/rename")
    @Operation(summary = "Rename a session", description = "Renames a chat session")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session renamed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid title"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<SessionResponse>> renameSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @Parameter(description = "New title") @RequestParam String title) {
        SessionResponse session = sessionService.renameSession(sessionId, title);
        return ResponseEntity.ok(ApiResponse.success("Session renamed successfully", session));
    }

    @PatchMapping("/{sessionId}/favorite")
    @Operation(summary = "Toggle favorite status", description = "Toggles the favorite status of a chat session")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Favorite status toggled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<SessionResponse>> toggleFavorite(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        SessionResponse session = sessionService.toggleFavorite(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Favorite status toggled successfully", session));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Delete a session", description = "Deletes a chat session and all its messages")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", null));
    }
}
