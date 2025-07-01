package com.swpteam.smokingcessation.feature.version1.blog.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentCreateRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentReplyRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentResponse;
import com.swpteam.smokingcessation.domain.dto.comment.CommentUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Comment;

public interface ICommentService {

    PageResponse<CommentResponse> getCommentsByBlogId(String blogId, PageableRequest request);

    PageResponse<CommentResponse> getCommentPage(PageableRequest request);

    CommentResponse getCommentById(String id);

    CommentResponse createComment(CommentCreateRequest request);

    CommentResponse replyComment(CommentReplyRequest request);

    CommentResponse updateComment(String id, CommentUpdateRequest request);

    void deleteCommentById(String id);

    Comment findCommentByIdOrThrowError(String id);
}
