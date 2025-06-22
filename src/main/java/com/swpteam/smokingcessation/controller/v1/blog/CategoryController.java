package com.swpteam.smokingcessation.controller.v1.blog;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.category.CategoryRequest;
import com.swpteam.smokingcessation.domain.dto.category.CategoryResponse;
import com.swpteam.smokingcessation.service.interfaces.blog.ICategoryService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @GetMapping("/list-all")
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryList(
            @Valid PageableRequest request)
    {
        return ResponseUtil.buildResponse(
                SuccessCode.CATEGORY_LIST_ALL,
                categoryService.getCategoryList()
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<Page<CategoryResponse>>> getCategoryPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.CATEGORY_GET_ALL,
                categoryService.getCategoryPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.CATEGORY_GET_BY_ID,
                categoryService.getCategoryById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody @Valid CategoryRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.CATEGORY_CREATED,
                categoryService.createCategory(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid CategoryRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.CATEGORY_UPDATED,
                categoryService.updateCategory(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteCategoryById(
            @PathVariable String id
    ) {
        categoryService.deleteCategoryById(id);
        return ResponseUtil.buildResponse(
                SuccessCode.CATEGORY_DELETED,
                null
        );
    }

}
