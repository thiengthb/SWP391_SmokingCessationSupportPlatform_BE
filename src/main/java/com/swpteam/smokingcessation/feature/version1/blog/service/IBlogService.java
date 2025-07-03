package com.swpteam.smokingcessation.feature.version1.blog.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Blog;

public interface IBlogService {

    PageResponse<BlogResponse> getAllBlogsPage(PageableRequest request);

    PageResponse<BlogResponse> getMyBlogsPage(PageableRequest request);

    PageResponse<BlogResponse> getBlogsPageByCategory(String categoryName, PageableRequest request);

    BlogResponse getBlogBySlug(String slug);

    BlogResponse getBlogById(String id);

    BlogResponse createBlog(BlogCreateRequest request);

    BlogResponse updateBlog(String id, BlogUpdateRequest request);

    void softDeleteBlogById(String id);

    Blog findBlogBySlugOrThrowError(String slug);

    Blog findBlogByIdOrThrowError(String id);

}
