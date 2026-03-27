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
import java.util.concurrent.ExecutionException;

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
     * Create a new project asynchronously.
     */
    @PostMapping
    @Operation(summary = "Create project", description = "Creates a new project with the provided details")
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody ProjectCreateDTO createDTO) throws ExecutionException, InterruptedException {
        ProjectDTO createdProject = projectService.createProject(createDTO).get();
        ApiResponse<ProjectDTO> response = ApiResponse.success(createdProject, "Project created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve all projects asynchronously.
     */
    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieves all projects in the system")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects() throws ExecutionException, InterruptedException {
        List<ProjectDTO> projects = projectService.getAllProjects().get();
        ApiResponse<List<ProjectDTO>> response = ApiResponse.success(projects,
                String.format("Retrieved %d projects", projects.size()));
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve project by ID asynchronously.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieves a project by its ID")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@Parameter(description = "Project ID") @PathVariable Long id) throws ExecutionException, InterruptedException {
        ProjectDTO project = projectService.getProjectById(id).get();
        ApiResponse<ProjectDTO> response = ApiResponse.success(project);
        return ResponseEntity.ok(response);
    }
}