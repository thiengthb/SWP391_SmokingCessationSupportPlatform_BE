package com.swpteam.smokingcessation.feature.repository;

import com.swpteam.smokingcessation.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository {

    Optional<Category> findByIdAndIsDeletedFalse(String id);

    Page<Category> findByCreatedByAndIsDeletedFalse(String createdBy, Pageable pageable);

    Page<Category> findAllByIsDeletedFalse(Pageable pageable);
}
