package com.swpteam.smokingcessation.feature.repository;

import com.swpteam.smokingcessation.domain.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<Blog, String> {

    Optional<Blog> findByIdAndIsDeletedFalse(String id);

    Page<Blog> findByCreatedByAndIsDeletedFalse(String createdBy, Pageable pageable);

    Page<Blog> findByAuthorNameAndIsDeletedFalse(String authorName, Pageable pageable);

    Page<Blog> findAllByIsDeletedFalse(Pageable pageable);
}
