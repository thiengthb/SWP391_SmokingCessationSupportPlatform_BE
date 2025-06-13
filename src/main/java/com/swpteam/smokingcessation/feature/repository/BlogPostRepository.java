package com.swpteam.smokingcessation.feature.repository;

import com.swpteam.smokingcessation.domain.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, String> {

    Optional<BlogPost> findByIdAndIsDeletedFalse(String id);

    Page<BlogPost> findByCreatedByAndIsDeletedFalse(String createdBy, Pageable pageable);

    Page<BlogPost> findByAuthorNameAndIsDeletedFalse(String authorName, Pageable pageable);

    Page<BlogPost> findAllByIsDeletedFalse(Pageable pageable);
}
