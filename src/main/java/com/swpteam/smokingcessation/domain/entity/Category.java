package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseEntity {

    @Column(nullable = false, length = 100)
    String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    List<Blog> blogs;
}
