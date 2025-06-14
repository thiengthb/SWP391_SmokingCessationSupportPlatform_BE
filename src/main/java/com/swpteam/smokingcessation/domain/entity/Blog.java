package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.swpteam.smokingcessation.common.BaseEntity;
import com.swpteam.smokingcessation.domain.enums.BlogStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Blog extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    @JsonBackReference
    Account account;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "categoryId", nullable = false)
    Category category;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Comment> comments;

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
