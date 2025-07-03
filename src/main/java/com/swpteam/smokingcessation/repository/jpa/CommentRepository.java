package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>  {

    Page<Comment> findByAccountId(String accountId, Pageable pageable);

    Page<Comment> findByBlogIdAndLevel(String blogId, int level, Pageable pageable);

    Page<Comment> findAll(Pageable pageable);
}
