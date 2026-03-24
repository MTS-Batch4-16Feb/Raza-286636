package org.ust.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ust.task.entity.TaskStatus;
import org.ust.task.entity.TaskPriority;
import java.time.LocalDateTime;

/**
 * DTO for creating a new Task.
 */
public record TaskCreateDTO(
    @NotBlank(message = "Task title is required")
    String title,
    
    String description,
    
    @NotNull(message = "Task status is required")
    TaskStatus status,
    
    @NotNull(message = "Task priority is required")
    TaskPriority priority,
    
    LocalDateTime dueDate,
    
    @NotNull(message = "Project ID is required")
    Long projectId,
    
    Long assigneeId
) {}

