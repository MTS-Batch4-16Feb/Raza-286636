package org.ust.task.service;

import org.ust.task.dto.CommentCreateDTO;
import org.ust.task.dto.CommentDTO;

import java.util.List;

/**
 * Service interface for Comment management operations
 */
public interface CommentServiceInterface {
    
    /**
     * Create a new comment on a task
     */
    CommentDTO createComment(CommentCreateDTO createDTO);
    
    /**
     * Retrieve comment by ID
     */
    CommentDTO getCommentById(Long id);
    
    /**
     * Retrieve all comments
     */
    List<CommentDTO> getAllComments();
    
    /**
     * Retrieve all comments for a specific task
     */
    List<CommentDTO> getCommentsByTaskId(Long taskId);
    
    /**
     * Retrieve all comments created by a specific user
     */
    List<CommentDTO> getCommentsByUserId(Long userId);
    
    /**
     * Update comment content
     */
    CommentDTO updateComment(Long id, String newContent);
    
    /**
     * Delete comment
     */
    void deleteComment(Long id);
}

