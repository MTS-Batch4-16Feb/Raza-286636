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

/**
 * REST Controller for Comment management endpoints.
 * Handles all HTTP requests related to comment operations.
 */
@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comment Management", description = "Endpoints for managing comments on tasks")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentServiceInterface commentService;
    
    /**
     * Create a new comment.
     *
     * @param createDTO Comment creation data
     * @return Created comment response with 201 status
     */
    @PostMapping
    @Operation(summary = "Create comment", description = "Creates a new comment on a task")
    public ResponseEntity<ApiResponse<CommentDTO>> createComment(@Valid @RequestBody CommentCreateDTO createDTO) {
        CommentDTO createdComment = commentService.createComment(createDTO);
        ApiResponse<CommentDTO> response = ApiResponse.success(createdComment, "Comment created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Retrieve all comments.
     *
     * @return List of all comments
     */
    @GetMapping
    @Operation(summary = "Get all comments", description = "Retrieves all comments in the system")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getAllComments() {
        List<CommentDTO> comments = commentService.getAllComments();
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments,
            String.format("Retrieved %d comments", comments.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve comment by ID.
     *
     * @param id Comment ID
     * @return Comment data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID", description = "Retrieves a comment by its ID")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentById(@Parameter(description = "Comment ID") @PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id);
        ApiResponse<CommentDTO> response = ApiResponse.success(comment);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve all comments for a specific task.
     *
     * @param taskId Task ID
     * @return List of comments for the task
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get comments by task", description = "Retrieves all comments for a specific task")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByTaskId(@Parameter(description = "Task ID") @PathVariable Long taskId) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(taskId);
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments,
            String.format("Retrieved %d comments for task", comments.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve all comments created by a specific user.
     *
     * @param userId User ID
     * @return List of comments created by the user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get comments by user", description = "Retrieves all comments created by a specific user")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByUserId(@Parameter(description = "User ID") @PathVariable Long userId) {
        List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments,
            String.format("Retrieved %d comments by user", comments.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update comment content.
     *
     * @param id Comment ID to update
     * @param newContent New comment content
     * @return Updated comment data
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update comment", description = "Updates the content of a comment")
    public ResponseEntity<ApiResponse<CommentDTO>> updateComment(
            @Parameter(description = "Comment ID") @PathVariable Long id,
            @RequestBody String newContent) {
        
        CommentDTO updatedComment = commentService.updateComment(id, newContent);
        ApiResponse<CommentDTO> response = ApiResponse.success(updatedComment, "Comment updated successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete comment by ID.
     *
     * @param id Comment ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Deletes a comment by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@Parameter(description = "Comment ID") @PathVariable Long id) {
        commentService.deleteComment(id);
        ApiResponse<Void> response = ApiResponse.success("Comment deleted successfully");
        return ResponseEntity.ok(response);
    }
}

