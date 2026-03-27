package org.ust.task.service;

import org.ust.task.dto.ProjectCreateDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.ProjectUpdateDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for Project management operations
 */
public interface ProjectServiceInterface {

    /**
     * Create a new project asynchronously
     */
    CompletableFuture<ProjectDTO> createProject(ProjectCreateDTO createDTO);

    /**
     * Create a new project with explicit owner ID asynchronously
     */
    CompletableFuture<ProjectDTO> createProject(ProjectCreateDTO createDTO, Long ownerId);

    /**
     * Retrieve project by ID asynchronously
     */
    CompletableFuture<ProjectDTO> getProjectById(Long id);

    /**
     * Retrieve all projects asynchronously
     */
    CompletableFuture<List<ProjectDTO>> getAllProjects();

    /**
     * Retrieve project with its tasks asynchronously
     */
    CompletableFuture<ProjectDTO> getProjectWithTasks(Long id);

    /**
     * Retrieve all projects owned by a specific user asynchronously
     */
    CompletableFuture<List<ProjectDTO>> getProjectsByOwnerId(Long ownerId);

    /**
     * Update existing project asynchronously
     */
    CompletableFuture<ProjectDTO> updateProject(Long id, ProjectUpdateDTO updateDTO);

    /**
     * Partially update project asynchronously
     */
    CompletableFuture<ProjectDTO> partialUpdateProject(Long id, ProjectUpdateDTO updateDTO);

    /**
     * Delete project asynchronously
     */
    CompletableFuture<Void> deleteProject(Long id);
}