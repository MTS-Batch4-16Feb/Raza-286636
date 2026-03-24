package org.ust.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ust.task.entity.TaskStatus;
import org.ust.task.entity.TaskPriority;
import java.time.LocalDateTime;

/**
 * DTO for updating Task information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdateDTO {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Long projectId;
    private Long assigneeId;
}

