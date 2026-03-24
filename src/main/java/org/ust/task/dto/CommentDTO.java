package org.ust.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for Comment response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private Long taskId;
    private UserDTO user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

