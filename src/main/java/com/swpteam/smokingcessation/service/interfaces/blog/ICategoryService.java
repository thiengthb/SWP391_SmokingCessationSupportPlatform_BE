package com.swpteam.smokingcessation.service.interfaces.blog;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.category.CategoryRequest;
import com.swpteam.smokingcessation.domain.dto.category.CategoryResponse;
import com.swpteam.smokingcessation.domain.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICategoryService {

    List<CategoryResponse> getCategoryList();

    PageResponse<CategoryResponse> getCategoryPage(PageableRequest request);

    CategoryResponse getCategoryById(String id);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(String id, CategoryRequest request);

    void deleteCategoryById(String id);

    Category findCategoryByIdOrThrowError(String id);

    Category findCategoryByNameOrThrowError(String name);
}
