package org.ust.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new Comment.
 */
public record CommentCreateDTO(
    @NotBlank(message = "Comment content is required")
    String content,
    
    @NotNull(message = "Task ID is required")
    Long taskId,
    
    @NotNull(message = "User ID is required")
    Long userId
) {}

