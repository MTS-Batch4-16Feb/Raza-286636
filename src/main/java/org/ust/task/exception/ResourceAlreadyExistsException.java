package org.ust.task.exception;

/**
 * Exception thrown when attempting to create a resource that already exists (e.g., duplicate email or username).
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    /**
     * User with email already exists
     */
    public static ResourceAlreadyExistsException userEmail(String email) {
        return new ResourceAlreadyExistsException("User", "email", email);
    }
    
    /**
     * User with username already exists
     */
    public static ResourceAlreadyExistsException userUsername(String username) {
        return new ResourceAlreadyExistsException("User", "username", username);
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

