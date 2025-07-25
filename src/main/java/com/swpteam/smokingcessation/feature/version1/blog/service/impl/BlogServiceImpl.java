package com.swpteam.smokingcessation.feature.version1.blog.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.App;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.blog.BlogCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogDetailsResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogListItemResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Blog;
import com.swpteam.smokingcessation.domain.entity.Category;
import com.swpteam.smokingcessation.domain.mapper.BlogMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.blog.service.HtmlSanitizerService;
import com.swpteam.smokingcessation.repository.jpa.BlogRepository;
import com.swpteam.smokingcessation.repository.jpa.CategoryRepository;
import com.swpteam.smokingcessation.feature.version1.blog.service.IBlogService;
import com.swpteam.smokingcessation.feature.version1.blog.service.ICategoryService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.SlugUtil;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogServiceImpl implements IBlogService {

    BlogMapper blogMapper;
    BlogRepository blogRepository;
    CategoryRepository categoryRepository;
    ICategoryService categoryService;
    AuthUtilService authUtilService;

    HtmlSanitizerService htmlSanitizerService;

    @Override
    @Cacheable(value = "BLOG_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<BlogListItemResponse> getAllBlogsPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Blog.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Blog> blogs = blogRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(blogs.map(blogMapper::toListItemResponse));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "BLOG_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + @authUtilService.getCurrentAccountOrThrowError().id")
    public PageResponse<BlogListItemResponse> getMyBlogsPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Blog.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Blog> blogs = blogRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(blogs.map(blogMapper::toListItemResponse));
    }

    @Override
    @Cacheable(value = "BLOG_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + #categoryName")
    public PageResponse<BlogListItemResponse> getBlogsPageByCategory(String categoryName, PageableRequest request) {
        Category category = categoryService.findCategoryByNameOrThrowError(categoryName);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Blog> blogs = blogRepository.findByCategoryIdAndIsDeletedFalse(category.getId(), pageable);

        return new PageResponse<>(blogs.map(blogMapper::toListItemResponse));
    }

    @Override
    @Cacheable(value = "BLOG_CACHE", key = "#slug")
    public BlogDetailsResponse getBlogBySlug(String slug) {
        return blogMapper.toResponse(findBlogBySlugOrThrowError(slug));
    }

    @Override
    @Cacheable(value = "BLOG_CACHE", key = "#id")
    public BlogDetailsResponse getBlogById(String id) {
        return blogMapper.toResponse(findBlogByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "BLOG_CACHE", key = "#result.getId()")
    @CacheEvict(value = "BLOG_PAGE_CACHE", allEntries = true)
    public BlogDetailsResponse createBlog(BlogCreateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Category category = categoryRepository.findByName(request.categoryName())
                .or(() -> categoryRepository.findByName(App.DEFAULT_CATEGORY))
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Blog blog = blogMapper.toEntity(request);

        blog.setContent(htmlSanitizerService.sanitize(request.content()));

        blog.setAccount(currentAccount);
        blog.setCategory(category);

        String baseSlug = SlugUtil.toSlug(blog.getTitle());
        blog.setSlug(ensureUniqueSlug(baseSlug));

        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "BLOG_CACHE", key = "#result.getId()")
    @CacheEvict(value = "BLOG_PAGE_CACHE", allEntries = true)
    public BlogDetailsResponse updateBlog(String id, BlogUpdateRequest request) {
        Blog blog = findBlogByIdOrThrowError(id);

        blogMapper.update(blog, request);

        blog.setContent(htmlSanitizerService.sanitize(request.content()));

        String baseSlug = SlugUtil.toSlug(blog.getTitle());
        blog.setSlug(ensureUniqueSlug(baseSlug));

        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"BLOG_CACHE", "BLOG_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void softDeleteBlogById(String id) {
        Blog blog = findBlogByIdOrThrowError(id);

        blog.setDeleted(true);

        blogRepository.save(blog);
    }

    @Override
    @Transactional
    public Blog findBlogBySlugOrThrowError(String slug) {
        Blog blog =  blogRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (blog.getAccount().isDeleted()) {
            blog.setDeleted(true);
            blogRepository.save(blog);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return blog;
    }

    @Override
    @Transactional
    public Blog findBlogByIdOrThrowError(String id) {
        Blog blog = blogRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (blog.getAccount().isDeleted()) {
            blog.setDeleted(true);
            blogRepository.save(blog);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return blog;
    }

    private String ensureUniqueSlug(String slug) {
        String temp = slug;

        while (blogRepository.existsBySlug(temp)) {
            temp = SlugUtil.appendRandomSuffix(slug);
        }
        return temp;
    }
}
