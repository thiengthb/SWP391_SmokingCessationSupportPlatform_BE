package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findByName(String name);

    Page<Category> findAll(Pageable pageable);

    boolean existsByName(String name);
}
