package com.swpteam.smokingcessation.feature.version1.blog.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogDetailsResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogListItemResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Blog;

public interface IBlogService {

    PageResponse<BlogListItemResponse> getAllBlogsPage(PageableRequest request);

    PageResponse<BlogListItemResponse> getMyBlogsPage(PageableRequest request);

    PageResponse<BlogListItemResponse> getBlogsPageByCategory(String categoryName, PageableRequest request);

    BlogDetailsResponse getBlogBySlug(String slug);

    BlogDetailsResponse getBlogById(String id);

    BlogDetailsResponse createBlog(BlogCreateRequest request);

    BlogDetailsResponse updateBlog(String id, BlogUpdateRequest request);

    void softDeleteBlogById(String id);

    Blog findBlogBySlugOrThrowError(String slug);

    Blog findBlogByIdOrThrowError(String id);

}
