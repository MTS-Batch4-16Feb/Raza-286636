package org.ust.task.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * User not found by ID
     */
    public static ResourceNotFoundException user(Long id) {
        return new ResourceNotFoundException("User", "id", id);
    }
    
    /**
     * User not found by username
     */
    public static ResourceNotFoundException userByUsername(String username) {
        return new ResourceNotFoundException("User", "username", username);
    }
    
    /**
     * User not found by email
     */
    public static ResourceNotFoundException userByEmail(String email) {
        return new ResourceNotFoundException("User", "email", email);
    }
    
    /**
     * Project not found by ID
     */
    public static ResourceNotFoundException project(Long id) {
        return new ResourceNotFoundException("Project", "id", id);
    }
    
    /**
     * Task not found by ID
     */
    public static ResourceNotFoundException task(Long id) {
        return new ResourceNotFoundException("Task", "id", id);
    }
    
    /**
     * Comment not found by ID
     */
    public static ResourceNotFoundException comment(Long id) {
        return new ResourceNotFoundException("Comment", "id", id);
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}

