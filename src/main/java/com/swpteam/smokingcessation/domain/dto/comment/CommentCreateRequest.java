package com.swpteam.smokingcessation.domain.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest (

    @NotBlank(message = "COMMENT_CONTENT_REQUIRED")
    String content,

    @NotBlank(message = "COMMENT_BLOG_ID_REQUIRED")
    String blogId
) {}
