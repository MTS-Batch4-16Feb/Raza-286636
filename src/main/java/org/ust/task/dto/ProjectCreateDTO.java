package org.ust.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for creating a new Project.
 */
public record ProjectCreateDTO(
    @NotBlank(message = "Project name is required")
    String name,
    
    String description,
    
    LocalDateTime startDate,
    
    LocalDateTime endDate,
    
    @NotBlank(message = "Project status is required")
    String status,
    
    @NotNull(message = "Owner ID is required")
    Long ownerId
) {}

