package org.ust.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ust.task.dto.UserCreateDTO;
import org.ust.task.dto.UserDTO;
import org.ust.task.dto.UserUpdateDTO;
import org.ust.task.entity.User;
import java.util.List;

/**
 * MapStruct mapper for User entity and DTOs.
 * Handles conversions between User entity and various DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    /**
     * Convert User entity to UserDTO.
     */
    UserDTO toDTO(User user);
    
    /**
     * Convert UserCreateDTO to User entity.
     * Ignores auto-generated fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "accountLocked", ignore = true)
    User toEntity(UserCreateDTO userCreateDTO);
    
    /**
     * Update User entity from UserUpdateDTO.
     * Null values in the DTO are ignored.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
    
    /**
     * Convert list of User entities to UserDTOs.
     */
    List<UserDTO> toDTOList(List<User> users);
}



