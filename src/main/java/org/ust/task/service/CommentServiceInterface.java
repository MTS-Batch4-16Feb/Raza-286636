package org.ust.task.service;

import org.ust.task.dto.CommentCreateDTO;
import org.ust.task.dto.CommentDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for Comment management operations.
 */
public interface CommentServiceInterface {

    /**
     * Create a new comment on a task.
     */
    CompletableFuture<CommentDTO> createComment(CommentCreateDTO createDTO);

    /**
     * Retrieve comment by ID.
     */
    CompletableFuture<CommentDTO> getCommentById(Long id);

    /**
     * Retrieve all comments.
     */
    CompletableFuture<List<CommentDTO>> getAllComments();

    /**
     * Retrieve all comments for a specific task.
     */
    CompletableFuture<List<CommentDTO>> getCommentsByTaskId(Long taskId);

    /**
     * Retrieve all comments created by a specific user.
     */
    CompletableFuture<List<CommentDTO>> getCommentsByUserId(Long userId);

    /**
     * Update comment content.
     */
    CompletableFuture<CommentDTO> updateComment(Long id, String newContent);

    /**
     * Delete comment.
     */
    CompletableFuture<Void> deleteComment(Long id);
}