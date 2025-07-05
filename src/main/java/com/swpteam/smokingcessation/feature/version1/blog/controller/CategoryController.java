package com.swpteam.smokingcessation.feature.version1.blog.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.category.CategoryRequest;
import com.swpteam.smokingcessation.domain.dto.category.CategoryResponse;
import com.swpteam.smokingcessation.feature.version1.blog.service.ICategoryService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Category", description = "Manage category-related operations")
public class CategoryController {

    ICategoryService categoryService;
    ResponseUtilService responseUtilService;

    @GetMapping("/list-all")
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryList(
            @Valid PageableRequest request)
    {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CATEGORY_LIST_FETCHED,
                categoryService.getCategoryList()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getCategoryPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CATEGORY_PAGE_FETCHED,
                categoryService.getCategoryPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CATEGORY_FETCHED_BY_ID,
                categoryService.getCategoryById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody @Valid CategoryRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CATEGORY_CREATED,
                categoryService.createCategory(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid CategoryRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CATEGORY_UPDATED,
                categoryService.updateCategory(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteCategoryById(
            @PathVariable String id
    ) {
        categoryService.deleteCategoryById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CATEGORY_DELETED
        );
    }

}
