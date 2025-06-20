package com.swpteam.smokingcessation.service.impl.blog;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.App;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.blog.BlogCreateRequest;
import com.swpteam.smokingcessation.domain.dto.blog.BlogResponse;
import com.swpteam.smokingcessation.domain.dto.blog.BlogUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Blog;
import com.swpteam.smokingcessation.domain.entity.Category;
import com.swpteam.smokingcessation.domain.mapper.BlogMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.BlogRepository;
import com.swpteam.smokingcessation.repository.CategoryRepository;
import com.swpteam.smokingcessation.service.interfaces.blog.IBlogService;
import com.swpteam.smokingcessation.service.interfaces.blog.ICategoryService;
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

    BlogRepository blogRepository;
    BlogMapper blogMapper;

    CategoryRepository categoryRepository;
    ICategoryService categoryService;

    AuthUtilService authUtilService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<BlogResponse> getAllBlogsPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Blog.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Blog> blogs = blogRepository.findAllByIsDeletedFalse(pageable);

        return blogs.map(blogMapper::toResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<BlogResponse> getMyBlogsPage(PageableRequest request) {
        Account account = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Blog> blogs = blogRepository.findAllByAccountIdAndIsDeletedFalse(account.getId(), pageable);

        return blogs.map(blogMapper::toResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<BlogResponse> getBlogsPageByCategory(String categoryName, PageableRequest request) {
        Category category = categoryService.findCategoryByNameOrThrowError(categoryName);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Blog> blogs = blogRepository.findByCategoryIdAndIsDeletedFalse(category.getId(), pageable);

        return blogs.map(blogMapper::toResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "BLOG_CACHE", key = "#slug")
    public BlogResponse getBlogBySlug(String slug) {
        return blogMapper.toResponse(findBlogBySlugOrThrowError(slug));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "BLOG_CACHE", key = "#id")
    public BlogResponse getBlogById(String id) {
        return blogMapper.toResponse(findBlogByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "BLOG_CACHE", key = "#result.getId()")
    public BlogResponse createBlog(BlogCreateRequest request) {
        Account account = authUtilService.getCurrentAccountOrThrowError();

        Category category = categoryRepository.findByName(request.categoryName())
                .or(() -> categoryRepository.findByName(App.DEFAULT_CATEGORY))
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Blog blog = blogMapper.toEntity(request);

        blog.setAccount(account);
        blog.setCategory(category);

        String baseSlug = SlugUtil.toSlug(blog.getTitle());
        blog.setSlug(ensureUniqueSlug(baseSlug));

        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "BLOG_CACHE", key = "#result.getId()")
    public BlogResponse updateBlog(String id, BlogUpdateRequest request) {
        Blog blog = findBlogByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isAdminOrOwner(blog.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.OTHERS_COMMENT_UNCHANGEABLE);
        }

        blogMapper.update(blog, request);

        String baseSlug = SlugUtil.toSlug(blog.getTitle());
        blog.setSlug(ensureUniqueSlug(baseSlug));

        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "BLOG_CACHE", key = "#id")
    public void deleteBlogById(String id) {
        Blog blog = findBlogByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isAdminOrOwner(blog.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.OTHERS_COMMENT_UNCHANGEABLE);
        }

        if (blog.getAccount().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }

        blog.setDeleted(true);
        blogRepository.save(blog);
    }

    @Override
    @Cacheable(value = "BLOG_CACHE", key = "#slug")
    public Blog findBlogBySlugOrThrowError(String slug) {
        return blogRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
    }

    @Override
    @Cacheable(value = "BLOG_CACHE", key = "#id")
    public Blog findBlogByIdOrThrowError(String id) {
        return blogRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
    }

    private String ensureUniqueSlug(String slug) {
        String temp = slug;

        while (blogRepository.existsBySlug(temp)) {
            temp = SlugUtil.appendRandomSuffix(slug);
        }
        return temp;
    }
}
