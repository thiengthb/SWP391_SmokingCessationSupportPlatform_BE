package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.blog.BlogCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogDetailsResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogListItemResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface BlogMapper {

    Blog toEntity(BlogCreateRequest request);

    @Mapping(source = "account.username", target = "authorName")
    @Mapping(source = "category.name", target = "categoryName")
    BlogDetailsResponse toResponse(Blog blog);

    @Mapping(source = "account.username", target = "authorName")
    @Mapping(source = "category.name", target = "categoryName")
    BlogListItemResponse toListItemResponse(Blog blog);

    void update(@MappingTarget Blog blog, BlogUpdateRequest request);
}
