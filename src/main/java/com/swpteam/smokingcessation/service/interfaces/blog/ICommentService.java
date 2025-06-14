package com.swpteam.smokingcessation.service.interfaces.blog;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentService {

    Page<CommentResponse> getCommentsByBlogId(String blogId, Pageable pageable);

    Page<CommentResponse> getCommentPage(PageableRequest request);

    CommentResponse getCommentById(String id);

    CommentResponse createComment(CommentRequest request);

    CommentResponse updateComment(String id, CommentRequest request);

    void deleteCommentById(String id);
}
