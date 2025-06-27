package com.swpteam.smokingcessation.controller.v1.blog;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.comment.CommentCreateRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentReplyRequest;
import com.swpteam.smokingcessation.domain.dto.comment.CommentResponse;
import com.swpteam.smokingcessation.domain.dto.comment.CommentUpdateRequest;
import com.swpteam.smokingcessation.service.interfaces.blog.ICommentService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Comment", description = "Manage comment-related operations")
public class CommentController {

    ICommentService commentService;

    @GetMapping("/blog/{id}")
    ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getCommentsByBlogId(
            @PathVariable String id,
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COMMENT_GET_BY_BLOG,
                commentService.getCommentsByBlogId(id, request)
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getCommentPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COMMENT_LIST_ALL,
                commentService.getCommentPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CommentResponse>> getCommentById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COMMENT_GET_BY_ID,
                commentService.getCommentById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<CommentResponse>> createSubscription(
            @RequestBody @Valid CommentCreateRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COMMENT_CREATED,
                commentService.createComment(request)
        );
    }

    @PostMapping("/reply")
    ResponseEntity<ApiResponse<CommentResponse>> createSubscription(
            @RequestBody @Valid CommentReplyRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COMMENT_CREATED,
                commentService.replyComment(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable String id,
            @RequestBody @Valid CommentUpdateRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COMMENT_UPDATED,
                commentService.updateComment(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteCommentById(
            @PathVariable String id
    ) {
        commentService.deleteCommentById(id);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.COMMENT_DELETED
        );
    }
    
}
