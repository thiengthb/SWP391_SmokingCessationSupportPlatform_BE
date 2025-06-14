package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.comment.CommentRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentResponse;
import com.swpteam.smokingcessation.domain.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toEntity(CommentRequest request);

    @Mapping(target = "commentChild", expression = "java(mapChildren(comment.getCommentChild()))")
    CommentResponse toResponse(Comment comment);

    default List<CommentResponse> mapChildren(List<Comment> children) {
        if (children == null || children.isEmpty()) return new ArrayList<>();
        return children.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    void update(@MappingTarget Comment comment, CommentRequest request);
}
