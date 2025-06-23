package com.swpteam.smokingcessation.domain.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentReplyRequest (

    @NotBlank(message = "COMMENT_CONTENT_REQUIRED")
    String content,

    @NotBlank(message = "COMMENT_REPLY_REQUIRED")
    String parentCommentId
) {}
