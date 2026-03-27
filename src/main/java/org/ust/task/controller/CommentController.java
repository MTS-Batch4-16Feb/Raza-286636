package org.ust.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ust.task.dto.CommentCreateDTO;
import org.ust.task.dto.CommentDTO;
import org.ust.task.response.ApiResponse;
import org.ust.task.service.CommentServiceInterface;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comment Management", description = "Endpoints for managing comments on tasks")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceInterface commentService;

    @PostMapping
    @Operation(summary = "Create comment", description = "Creates a new comment on a task")
    public CompletableFuture<ResponseEntity<ApiResponse<CommentDTO>>> createComment(@Valid @RequestBody CommentCreateDTO createDTO) {
        return commentService.createComment(createDTO)
                .thenApply(commentDTO -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(commentDTO, "Comment created successfully")))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred while creating the comment", "500")));
    }

    @GetMapping
    @Operation(summary = "Get all comments", description = "Retrieves all comments in the system")
    public CompletableFuture<ResponseEntity<ApiResponse<List<CommentDTO>>>> getAllComments() {
        return commentService.getAllComments()
                .thenApply(comments -> ResponseEntity.ok(ApiResponse.success(comments,
                        String.format("Retrieved %d comments", comments.size()))))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred while fetching comments", "500")));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID", description = "Retrieves a comment by its ID")
    public CompletableFuture<ResponseEntity<ApiResponse<CommentDTO>>> getCommentById(@Parameter(description = "Comment ID") @PathVariable Long id) {
        return commentService.getCommentById(id)
                .thenApply(commentDTO -> ResponseEntity.ok(ApiResponse.success(commentDTO)))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred while fetching the comment", "500")));
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get comments by task", description = "Retrieves all comments for a specific task")
    public CompletableFuture<ResponseEntity<ApiResponse<List<CommentDTO>>>> getCommentsByTaskId(@Parameter(description = "Task ID") @PathVariable Long taskId) {
        return commentService.getCommentsByTaskId(taskId)
                .thenApply(comments -> ResponseEntity.ok(ApiResponse.success(comments,
                        String.format("Retrieved %d comments for task", comments.size()))))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred while fetching comments for the task", "500")));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get comments by user", description = "Retrieves all comments created by a specific user")
    public CompletableFuture<ResponseEntity<ApiResponse<List<CommentDTO>>>> getCommentsByUserId(@Parameter(description = "User ID") @PathVariable Long userId) {
        return commentService.getCommentsByUserId(userId)
                .thenApply(comments -> ResponseEntity.ok(ApiResponse.success(comments,
                        String.format("Retrieved %d comments by user", comments.size()))))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred while fetching comments by user", "500")));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update comment", description = "Updates the content of a comment")
    public CompletableFuture<ResponseEntity<ApiResponse<CommentDTO>>> updateComment(
            @Parameter(description = "Comment ID") @PathVariable Long id,
            @RequestBody String newContent) {

        return commentService.updateComment(id, newContent)
                .thenApply(commentDTO -> ResponseEntity.ok(ApiResponse.success(commentDTO, "Comment updated successfully")))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred while updating the comment", "500")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Deletes a comment by ID")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> deleteComment(@Parameter(description = "Comment ID") @PathVariable Long id) {
        return commentService.deleteComment(id)
                .thenApply(aVoid -> ResponseEntity.ok(ApiResponse.success("Comment deleted successfully")))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred while deleting the comment", "500")));
    }
}