package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Blog;
import com.swpteam.smokingcessation.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, String> {

    Optional<Blog> findByIdAndIsDeletedFalse(String id);

    Page<Blog> findByCreatedByAndIsDeletedFalse(String createdBy, Pageable pageable);

    List<Blog> findByCategory(Category category);

    Page<Blog> findAllByIsDeletedFalse(Pageable pageable);
}
