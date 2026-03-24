package org.ust.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ust.task.dto.ProjectCreateDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.ProjectUpdateDTO;
import org.ust.task.entity.Project;
import java.util.List;

/**
 * MapStruct mapper for Project entity and DTOs.
 * Handles conversions between Project entity and various DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {
    
    /**
     * Convert Project entity to ProjectDTO.
     */
    @Mapping(target = "tasks", ignore = true)
    ProjectDTO toDTO(Project project);
    
    /**
     * Convert ProjectCreateDTO to Project entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toEntity(ProjectCreateDTO projectCreateDTO);
    
    /**
     * Update Project entity from ProjectUpdateDTO.
     * Null values in the DTO are ignored.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProjectUpdateDTO projectUpdateDTO, @MappingTarget Project project);
    
    /**
     * Convert list of Project entities to ProjectDTOs (without tasks).
     */
    @Mapping(target = "tasks", ignore = true)
    List<ProjectDTO> toDTOList(List<Project> projects);
}

