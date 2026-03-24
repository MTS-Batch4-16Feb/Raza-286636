package org.ust.task.service;

import org.ust.task.dto.ProjectCreateDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.ProjectUpdateDTO;

import java.util.List;

/**
 * Service interface for Project management operations
 */
public interface ProjectServiceInterface {
    
    /**
     * Create a new project
     */
    ProjectDTO createProject(ProjectCreateDTO createDTO);
    
    /**
     * Create a new project with explicit owner ID
     */
    ProjectDTO createProject(ProjectCreateDTO createDTO, Long ownerId);
    
    /**
     * Retrieve project by ID
     */
    ProjectDTO getProjectById(Long id);
    
    /**
     * Retrieve all projects
     */
    List<ProjectDTO> getAllProjects();
    
    /**
     * Retrieve project with its tasks
     */
    ProjectDTO getProjectWithTasks(Long id);
    
    /**
     * Retrieve all projects owned by a specific user
     */
    List<ProjectDTO> getProjectsByOwnerId(Long ownerId);
    
    /**
     * Update existing project
     */
    ProjectDTO updateProject(Long id, ProjectUpdateDTO updateDTO);
    
    /**
     * Partially update project
     */
    ProjectDTO partialUpdateProject(Long id, ProjectUpdateDTO updateDTO);
    
    /**
     * Delete project
     */
    void deleteProject(Long id);
}

