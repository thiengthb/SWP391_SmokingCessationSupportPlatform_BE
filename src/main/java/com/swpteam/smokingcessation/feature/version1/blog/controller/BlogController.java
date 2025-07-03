package com.swpteam.smokingcessation.feature.version1.blog.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.blog.BlogCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogUpdateRequest;
import com.swpteam.smokingcessation.feature.version1.blog.service.IBlogService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Blog", description = "Manage blog-related operations")
public class BlogController {

    IBlogService blogService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getAllBlogsPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BLOG_PAGE_FETCHED,
                blogService.getAllBlogsPage(request)
        );
    }

    @GetMapping("/my-blogs")
    ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getMyBlogsPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MY_BLOG_FETCHED,
                blogService.getMyBlogsPage(request)
        );
    }

    @GetMapping("/category/{name}")
    ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getBlogsPageByCategory(
            @PathVariable String name,
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BLOG_FETCHED_BY_CATEGORY,
                blogService.getBlogsPageByCategory(name, request)
        );
    }

    @GetMapping("/slug/{slugName}")
    ResponseEntity<ApiResponse<BlogResponse>> getBlogBySlug(
            @PathVariable String slugName
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BLOG_FETCHED_BY_SLUG,
                blogService.getBlogBySlug(slugName)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<BlogResponse>> getBlogById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BLOG_FETCHED_BY_ID,
                blogService.getBlogById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<BlogResponse>> createBlog(
            @RequestBody @Valid BlogCreateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BLOG_CREATED,
                blogService.createBlog(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<BlogResponse>> updateBlog(
            @PathVariable String id,
            @RequestBody @Valid BlogUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BLOG_UPDATED,
                blogService.updateBlog(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteBlogById(
            @PathVariable String id
    ) {
        blogService.softDeleteBlogById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.BLOG_DELETED
        );
    }

}
