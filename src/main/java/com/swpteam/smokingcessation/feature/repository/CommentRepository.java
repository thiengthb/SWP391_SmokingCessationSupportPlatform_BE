package com.swpteam.smokingcessation.feature.repository;

import com.swpteam.smokingcessation.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository {

    Optional<Comment> findByIdAndIsDeletedFalse(String id);

    Page<Comment> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<Comment> findAllByIsDeletedFalse(Pageable pageable);
}
