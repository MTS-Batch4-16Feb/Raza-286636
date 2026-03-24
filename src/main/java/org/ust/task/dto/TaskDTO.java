package org.ust.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ust.task.entity.TaskStatus;
import org.ust.task.entity.TaskPriority;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Task response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Long projectId;
    private UserDTO assignee;
    private List<CommentDTO> comments;
}

