package org.ust.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ust.task.dto.UserCreateDTO;
import org.ust.task.dto.UserDTO;
import org.ust.task.dto.UserUpdateDTO;
import org.ust.task.entity.Role;
import org.ust.task.exception.ResourceAlreadyExistsException;
import org.ust.task.response.ApiResponse;
import org.ust.task.service.UserServiceInterface;

import java.util.List;

/**
 * REST Controller for User management endpoints.
 * Handles all HTTP requests related to user operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserServiceInterface userService;
    
    /**
     * Create a new user account.
     *
     * @param createDTO User creation data
     * @return Created user response with 201 status
     * @throws ResourceAlreadyExistsException if email or username already exists
     */
    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user account with email and username validation")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        if (userService.existsByEmail(createDTO.email())) {
            throw ResourceAlreadyExistsException.userEmail(createDTO.email());
        }
        if (userService.existsByUsername(createDTO.username())) {
            throw ResourceAlreadyExistsException.userUsername(createDTO.username());
        }
        
        UserDTO createdUser = userService.createUser(createDTO);
        ApiResponse<UserDTO> response = ApiResponse.success(createdUser, "User created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Retrieve user by ID.
     *
     * @param id User ID
     * @return User data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@Parameter(description = "User ID") @PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        ApiResponse<UserDTO> response = ApiResponse.success(user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve all users.
     *
     * @return List of all users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users in the system")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        ApiResponse<List<UserDTO>> response = ApiResponse.success(users, 
            String.format("Retrieved %d users", users.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve user by email address.
     *
     * @param email User email
     * @return User data
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@Parameter(description = "User email") @PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        ApiResponse<UserDTO> response = ApiResponse.success(user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve users by role.
     *
     * @param role User role
     * @return List of users with specified role
     */
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves all users with a specific role")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@Parameter(description = "User role") @PathVariable Role role) {
        List<UserDTO> users = userService.getUsersByRole(role);
        ApiResponse<List<UserDTO>> response = ApiResponse.success(users, 
            String.format("Retrieved %d users with role '%s'", users.size(), role));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update user information.
     *
     * @param id User ID to update
     * @param updateDTO Update data
     * @return Updated user data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates a user's information")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        
        UserDTO updatedUser = userService.updateUser(id, updateDTO);
        ApiResponse<UserDTO> response = ApiResponse.success(updatedUser, "User updated successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete user by ID.
     *
     * @param id User ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = ApiResponse.success("User deleted successfully");
        return ResponseEntity.ok(response);
    }
}

