package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.BaseEntity;
import com.swpteam.smokingcessation.domain.enums.BlogStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogPost extends BaseEntity {

    @Size(max = 255)
    String authorName;

    @NotBlank
    @Size(max = 255)
    String title;

    @NotBlank
    @Size(max = 255)
    @Column(unique = true, nullable = false)
    String slug;

    String coverImageUrl;

    @Size(max = 500)
    String excerpt;

    @Lob
    String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    BlogStatus status;
}
