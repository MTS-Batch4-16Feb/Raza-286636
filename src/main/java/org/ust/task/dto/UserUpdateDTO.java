package org.ust.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ust.task.entity.Role;

/**
 * DTO for updating User information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    private String username;
    private String email;
    private String password;
    private Role role;
}

