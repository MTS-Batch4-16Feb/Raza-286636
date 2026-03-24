package org.ust.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ust.task.dto.TaskCreateDTO;
import org.ust.task.dto.TaskDTO;
import org.ust.task.dto.TaskUpdateDTO;
import org.ust.task.entity.Task;
import java.util.List;

/**
 * MapStruct mapper for Task entity and DTOs.
 * Handles conversions between Task entity and various DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    
    /**
     * Convert Task entity to TaskDTO.
     */
    @Mapping(target = "comments", ignore = true)
    TaskDTO toDTO(Task task);
    
    /**
     * Convert TaskCreateDTO to Task entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskCreateDTO taskCreateDTO);
    
    /**
     * Update Task entity from TaskCreateDTO.
     * Null values in the DTO are ignored.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(TaskCreateDTO taskCreateDTO, @MappingTarget Task task);
    
    /**
     * Update Task entity from TaskUpdateDTO.
     * Null values in the DTO are ignored.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);
    
    /**
     * Convert list of Task entities to TaskDTOs.
     */
    List<TaskDTO> toDTOList(List<Task> tasks);
}

