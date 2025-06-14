package com.swpteam.smokingcessation.service.impl.blog;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.comment.CommentRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Comment;
import com.swpteam.smokingcessation.domain.mapper.CommentMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.CommentRepository;
import com.swpteam.smokingcessation.service.interfaces.blog.ICommentService;
import com.swpteam.smokingcessation.utils.AccountUtilService;
import com.swpteam.smokingcessation.utils.AuthorizationUtilService;
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
public class CommentServiceImpl implements ICommentService {

    CommentRepository commentRepository;
    CommentMapper commentMapper;

    AccountUtilService accountUtilService;
    AuthorizationUtilService authorizationUtilService;

    @Override
    public Page<CommentResponse> getCommentsByBlogId(String blogId, PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Comment.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Comment> topLevelComments = commentRepository.findByBlogIdAndLevel(blogId, 0, pageable);

        return topLevelComments.map(commentMapper::toResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CommentResponse> getCommentPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Comment.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Comment> comments = commentRepository.findAll(pageable);

        return comments.map(commentMapper::toResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CommentResponse getCommentById(String id) {
        Comment comment = findCommentById(id);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    @CachePut(value = "COMMENT_CACHE", key = "#result.getId()")
    public CommentResponse createComment(CommentRequest request) {
        Account currentAccount = accountUtilService.getCurrentAccount()
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Comment comment = commentMapper.toEntity(request);
        comment.setAccount(currentAccount);

        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    @CachePut(value = "COMMENT_CACHE", key = "#result.getId()")
    public CommentResponse updateComment(String id, CommentRequest request) {
        Comment comment = findCommentById(id);

        boolean haveAccess = authorizationUtilService.checkAdminOrOwner(comment.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.OTHERS_COMMENT_UNCHANGEABLE);
        }

        commentMapper.update(comment, request);

        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    @CacheEvict(value = "COMMENT_CACHE", key = "#id")
    public void deleteCommentById(String id) {
        Comment comment = findCommentById(id);

        boolean haveAccess = authorizationUtilService.checkAdminOrOwner(comment.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.OTHERS_COMMENT_UNCHANGEABLE);
        }

        commentRepository.deleteById(id);
    }

    @Cacheable(value = "COMMENT_CACHE", key = "#id")
    private Comment findCommentById(String id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
