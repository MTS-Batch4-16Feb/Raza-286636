package org.ust.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.dto.UserCreateDTO;
import org.ust.task.dto.UserDTO;
import org.ust.task.dto.UserUpdateDTO;
import org.ust.task.entity.Role;
import org.ust.task.entity.User;
import org.ust.task.exception.ResourceAlreadyExistsException;
import org.ust.task.exception.ResourceNotFoundException;
import org.ust.task.mapper.UserMapper;
import org.ust.task.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for User entity operations.
 * Handles business logic, transaction management, and caching for user-related operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    /**
     * Create a new user with validation for email and username uniqueness.
     *
     * @param createDTO User creation data transfer object
     * @return Created UserDTO
     * @throws ResourceAlreadyExistsException if email or username already exists
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(UserCreateDTO createDTO) {
        validateUserUniqueness(createDTO.email(), createDTO.username());
        
        User user = userMapper.toEntity(createDTO);
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        return userMapper.toDTO(savedUser);
    }
    
    /**
     * Retrieve user by ID with caching.
     *
     * @param id User ID
     * @return UserDTO
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Cacheable(value = "user", key = "#id")
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
        return userMapper.toDTO(user);
    }
    
    /**
     * Retrieve user by username with caching.
     *
     * @param username User's username
     * @return UserDTO
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Cacheable(value = "user", key = "'username_' + #username")
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> ResourceNotFoundException.userByUsername(username));
        return userMapper.toDTO(user);
    }
    
    /**
     * Retrieve user by email with caching.
     *
     * @param email User's email address
     * @return UserDTO
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Cacheable(value = "user", key = "'email_' + #email")
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.userByEmail(email));
        return userMapper.toDTO(user);
    }
    
    /**
     * Update user information with validation for email/username uniqueness.
     *
     * @param id User ID to update
     * @param updateDTO Update data
     * @return Updated UserDTO
     * @throws ResourceNotFoundException if user not found
     * @throws ResourceAlreadyExistsException if new email/username already exists
     */
    @Override
    @CachePut(value = "user", key = "#id")
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
        
        validateEmailUniquenessForUpdate(existingUser, updateDTO.getEmail());
        validateUsernameUniquenessForUpdate(existingUser, updateDTO.getUsername());
        
        userMapper.updateEntityFromDTO(updateDTO, existingUser);
        
        User savedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }
    
    /**
     * Delete user by ID.
     *
     * @param id User ID to delete
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "user", key = "#id"),
        @CacheEvict(value = "users", allEntries = true)
    })
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.user(id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }
    
    /**
     * Retrieve all users with caching.
     *
     * @return List of all UserDTOs
     */
    @Override
    @Cacheable(value = "users", key = "'all'")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieve users by role with caching.
     *
     * @param role User role
     * @return List of UserDTOs with specified role
     */
    @Override
    @Cacheable(value = "users", key = "'role_' + #role")
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if email already exists.
     *
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Check if username already exists.
     *
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Validate email and username uniqueness.
     *
     * @param email Email to validate
     * @param username Username to validate
     * @throws ResourceAlreadyExistsException if either already exists
     */
    private void validateUserUniqueness(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw ResourceAlreadyExistsException.userEmail(email);
        }
        if (userRepository.existsByUsername(username)) {
            throw ResourceAlreadyExistsException.userUsername(username);
        }
    }
    
    /**
     * Validate email uniqueness for updates (ignore current user).
     *
     * @param user Current user entity
     * @param newEmail New email to validate
     * @throws ResourceAlreadyExistsException if email already exists and not owned by current user
     */
    private void validateEmailUniquenessForUpdate(User user, String newEmail) {
        if (newEmail != null && !newEmail.isBlank() && !user.getEmail().equals(newEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw ResourceAlreadyExistsException.userEmail(newEmail);
            }
        }
    }
    
    /**
     * Validate username uniqueness for updates (ignore current user).
     *
     * @param user Current user entity
     * @param newUsername New username to validate
     * @throws ResourceAlreadyExistsException if username already exists and not owned by current user
     */
    private void validateUsernameUniquenessForUpdate(User user, String newUsername) {
        if (newUsername != null && !newUsername.isBlank() && !user.getUsername().equals(newUsername)) {
            if (userRepository.existsByUsername(newUsername)) {
                throw ResourceAlreadyExistsException.userUsername(newUsername);
            }
        }
    }
}

