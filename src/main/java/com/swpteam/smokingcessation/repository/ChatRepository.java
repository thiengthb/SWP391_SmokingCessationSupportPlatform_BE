package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, String> {
    boolean existsByAccountIdAndIsDeletedFalse(String accountId);

    Page<Chat> findAllByIsDeletedFalse(Pageable pageable);

    Page<Chat> findByAccountIdAndIsDeletedFalse(String id, Pageable pageable);
}
