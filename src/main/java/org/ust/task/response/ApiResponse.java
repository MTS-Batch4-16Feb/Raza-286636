package org.ust.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Generic API response wrapper for all endpoints.
 * Provides consistent response structure across the application.
 *
 * @param <T> The type of data being returned
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;
    private String errorCode;
    
    /**
     * Create a successful response with data and message.
     *
     * @param data Response data
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * Create a successful response with data only.
     *
     * @param data Response data
     * @param <T> Type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }
    
    /**
     * Create a successful response with only message (for Void responses).
     *
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    /**
     * Create an error response.
     *
     * @param message Error message
     * @param errorCode Error code
     * @param <T> Type of data
     * @return ApiResponse with failure status
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}

