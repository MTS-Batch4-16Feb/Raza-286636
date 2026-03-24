package org.ust.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.ust.task.dto.CommentCreateDTO;
import org.ust.task.dto.CommentDTO;
import org.ust.task.entity.Comment;
import java.util.List;

/**
 * MapStruct mapper for Comment entity and DTOs.
 * Handles conversions between Comment entity and various DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    
    /**
     * Convert Comment entity to CommentDTO.
     */
    CommentDTO toDTO(Comment comment);
    
    /**
     * Convert CommentCreateDTO to Comment entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toEntity(CommentCreateDTO commentCreateDTO);
    
    /**
     * Convert list of Comment entities to CommentDTOs.
     */
    List<CommentDTO> toDTOList(List<Comment> comments);
}

