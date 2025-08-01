package com.swpteam.smokingcessation.domain.dto.blog;

import com.swpteam.smokingcessation.domain.enums.BlogStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BlogUpdateRequest (

    @Size(max = 255, message = "BLOG_TITLE_TOO_LONG")
    String title,
    
    String categoryName,

    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "BLOG_SLUG_INVALID")
    @Size(max = 255, message = "BLOG_SLUG_TOO_LONG")
    String slug,

    @Size(max = 1000, message = "BLOG_COVER_IMAGE_TOO_LONG")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "BLOG_COVER_IMAGE_INVALID"
    )
    String coverImageUrl,

    @Size(max = 500, message = "BLOG_EXCERPT_TOO_LONG")
    String excerpt,

    String content,

    String readingTime,

    BlogStatus status
) {}
