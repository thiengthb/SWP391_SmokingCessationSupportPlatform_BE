package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.blog.BlogPostCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogPostResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogPostUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BlogPostMapper {

    Blog toEntity(BlogPostCreateRequest request);

    BlogPostResponse toResponse(Blog membership);

    void update(@MappingTarget Blog membership, BlogPostUpdateRequest request);
}
