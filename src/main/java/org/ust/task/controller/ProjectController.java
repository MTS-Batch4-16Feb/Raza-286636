package org.ust.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ust.task.dto.ProjectCreateDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.ProjectUpdateDTO;
import org.ust.task.response.ApiResponse;
import org.ust.task.service.ProjectServiceInterface;

import java.util.List;

/**
 * REST Controller for Project management endpoints.
 * Handles all HTTP requests related to project operations.
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Project Management", description = "Endpoints for managing projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectServiceInterface projectService;
    
    /**
     * Create a new project.
     *
     * @param createDTO Project creation data
     * @return Created project response with 201 status
     */
    @PostMapping
    @Operation(summary = "Create project", description = "Creates a new project with the provided details")
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody ProjectCreateDTO createDTO) {
        ProjectDTO createdProject = projectService.createProject(createDTO);
        ApiResponse<ProjectDTO> response = ApiResponse.success(createdProject, "Project created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Retrieve all projects.
     *
     * @return List of all projects
     */
    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieves all projects in the system")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        ApiResponse<List<ProjectDTO>> response = ApiResponse.success(projects, 
            String.format("Retrieved %d projects", projects.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve project by ID.
     *
     * @param id Project ID
     * @return Project data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieves a project by its ID")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@Parameter(description = "Project ID") @PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        ApiResponse<ProjectDTO> response = ApiResponse.success(project);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve project with all its associated tasks.
     *
     * @param id Project ID
     * @return Project data including tasks
     */
    @GetMapping("/{id}/tasks")
    @Operation(summary = "Get project with tasks", description = "Retrieves a project along with all its tasks")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectWithTasks(@Parameter(description = "Project ID") @PathVariable Long id) {
        ProjectDTO project = projectService.getProjectWithTasks(id);
        ApiResponse<ProjectDTO> response = ApiResponse.success(project);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve all projects owned by a specific user.
     *
     * @param ownerId Owner user ID
     * @return List of projects owned by the user
     */
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get projects by owner", description = "Retrieves all projects owned by a specific user")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByOwner(@Parameter(description = "Owner user ID") @PathVariable Long ownerId) {
        List<ProjectDTO> projects = projectService.getProjectsByOwnerId(ownerId);
        ApiResponse<List<ProjectDTO>> response = ApiResponse.success(projects,
            String.format("Retrieved %d projects owned by user %d", projects.size(), ownerId));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update entire project with new data.
     *
     * @param id Project ID to update
     * @param updateDTO Update data
     * @return Updated project data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Updates all fields of a project")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDTO updateDTO) {
        
        ProjectDTO updatedProject = projectService.updateProject(id, updateDTO);
        ApiResponse<ProjectDTO> response = ApiResponse.success(updatedProject, "Project updated successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Partially update project with selected fields.
     *
     * @param id Project ID to update
     * @param updateDTO Update data (only non-null fields are updated)
     * @return Updated project data
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update project", description = "Updates only the provided fields of a project")
    public ResponseEntity<ApiResponse<ProjectDTO>> partialUpdateProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @RequestBody ProjectUpdateDTO updateDTO) {
        
        ProjectDTO updatedProject = projectService.partialUpdateProject(id, updateDTO);
        ApiResponse<ProjectDTO> response = ApiResponse.success(updatedProject, "Project partially updated");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete project by ID.
     *
     * @param id Project ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Deletes a project by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@Parameter(description = "Project ID") @PathVariable Long id) {
        projectService.deleteProject(id);
        ApiResponse<Void> response = ApiResponse.success("Project deleted successfully");
        return ResponseEntity.ok(response);
    }
}

