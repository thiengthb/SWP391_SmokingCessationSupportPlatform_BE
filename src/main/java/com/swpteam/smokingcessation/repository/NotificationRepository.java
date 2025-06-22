package com.swpteam.smokingcessation.repository;


import com.swpteam.smokingcessation.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    Optional<Notification> findByIdAndIsDeletedFalse(String id);

    List<Notification> findAllByAccountIdAndIsDeletedFalse(String id);

    Page<Notification> findAllByIsDeletedFalse(Pageable pageable);

    Page<Notification> findAllByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<Notification> findByAccountIdAndIsDeletedFalse(String id, Pageable pageable);
}
