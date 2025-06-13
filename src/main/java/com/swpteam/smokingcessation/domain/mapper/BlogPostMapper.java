package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.blog.BlogPostCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogPostResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogPostUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.BlogPost;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BlogPostMapper {

    BlogPost toEntity(BlogPostCreateRequest request);

    BlogPostResponse toResponse(BlogPost membership);

    void update(@MappingTarget BlogPost membership, BlogPostUpdateRequest request);
}
