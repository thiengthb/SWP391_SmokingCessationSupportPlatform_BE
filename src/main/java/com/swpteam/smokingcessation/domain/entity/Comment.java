package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
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
public class Comment extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    String content;

    int level;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "blogId", nullable = false)
    Blog blog;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    @ManyToOne
    @JoinColumn(name = "parentId")
    @JsonBackReference("parentChild")
    Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("parentChild")
    List<Comment> commentChild = new ArrayList<>();
}
