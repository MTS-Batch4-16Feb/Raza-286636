package org.ust.task.service;

import org.ust.task.dto.UserCreateDTO;
import org.ust.task.dto.UserDTO;
import org.ust.task.dto.UserUpdateDTO;
import org.ust.task.entity.Role;

import java.util.List;

/**
 * Service interface for User management operations
 */
public interface UserServiceInterface {
    
    /**
     * Create a new user with validation
     */
    UserDTO createUser(UserCreateDTO createDTO);
    
    /**
     * Retrieve user by ID
     */
    UserDTO getUserById(Long id);
    
    /**
     * Retrieve user by username
     */
    UserDTO getUserByUsername(String username);
    
    /**
     * Retrieve user by email
     */
    UserDTO getUserByEmail(String email);
    
    /**
     * Update existing user information
     */
    UserDTO updateUser(Long id, UserUpdateDTO updateDTO);
    
    /**
     * Delete user by ID
     */
    void deleteUser(Long id);
    
    /**
     * Retrieve all users
     */
    List<UserDTO> getAllUsers();
    
    /**
     * Retrieve users by role
     */
    List<UserDTO> getUsersByRole(Role role);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if user exists by username
     */
    boolean existsByUsername(String username);
}

