package com.swpteam.smokingcessation.feature.version1.blog.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.App;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.category.CategoryRequest;
import com.swpteam.smokingcessation.domain.dto.category.CategoryResponse;
import com.swpteam.smokingcessation.domain.entity.Blog;
import com.swpteam.smokingcessation.domain.entity.Category;
import com.swpteam.smokingcessation.domain.mapper.CategoryMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.BlogRepository;
import com.swpteam.smokingcessation.repository.jpa.CategoryRepository;
import com.swpteam.smokingcessation.feature.version1.blog.service.ICategoryService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements ICategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    BlogRepository blogRepository;

    @Override
    @Cacheable(value = "CATEGORY_LIST_CACHE")
    public List<CategoryResponse> getCategoryList() {
        return categoryRepository.findAll(Sort.by("name")).stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    @Cacheable(value = "CATEGORY_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<CategoryResponse> getCategoryPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Category.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Category> categories = categoryRepository.findAll(pageable);

        return new PageResponse<>(categories.map(categoryMapper::toResponse));
    }

    @Override
    @Cacheable(value = "CATEGORY_CACHE", key = "#id")
    public CategoryResponse getCategoryById(String id) {
        return categoryMapper.toResponse(findCategoryByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "CATEGORY_CACHE", key = "#result.getId()")
    @CacheEvict(value = {"CATEGORY_PAGE_CACHE", "CATEGORY_LIST_CACHE"}, allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "CATEGORY_CACHE", key = "#result.getId()")
    @CacheEvict(value = {"CATEGORY_PAGE_CACHE", "CATEGORY_LIST_CACHE"}, allEntries = true)
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = findCategoryByIdOrThrowError(id);

        categoryMapper.update(category, request);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"CATEGORY_CACHE", "CATEGORY_PAGE_CACHE", "CATEGORY_LIST_CACHE"}, key = "#id", allEntries = true)
    public void deleteCategoryById(String id) {
        Category category = findCategoryByIdOrThrowError(id);

        if (category.getName().equalsIgnoreCase(App.DEFAULT_CATEGORY)) {
            throw new AppException(ErrorCode.CATEGORY_DELETION_NOT_ALLOWED);
        }

        Category uncategorized = findCategoryByNameOrThrowError(App.DEFAULT_CATEGORY);

        List<Blog> blogs = blogRepository.findByCategoryId(category.getId());
        blogs.forEach(blog -> blog.setCategory(uncategorized));

        blogRepository.saveAll(blogs);

        categoryRepository.deleteById(id);
    }

    @Override
    public Category findCategoryByNameOrThrowError(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    public Category findCategoryByIdOrThrowError(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

}
