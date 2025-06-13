package com.swpteam.smokingcessation.domain.dto.blog;

import com.swpteam.smokingcessation.domain.enums.BlogStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogPostCreateRequest {

    @NotBlank(message = "Author name is required")
    @Size(max = 255, message = "Author name must not exceed 255 characters")
    String authorName;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase and can contain hyphens")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    String slug;

    @Size(max = 1000, message = "Cover image URL must not exceed 1000 characters")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Cover image URL must be a valid URL"
    )
    String coverImageUrl;

    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    String excerpt;

    @NotBlank(message = "Content is required")
    String content;

    @NotNull(message = "Status is required")
    BlogStatus status;
}
