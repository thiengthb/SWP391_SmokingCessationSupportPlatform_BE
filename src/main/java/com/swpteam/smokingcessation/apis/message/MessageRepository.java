package com.swpteam.smokingcessation.apis.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> IsDeletedFalse();

    Optional<Message> findByIdAndIsDeletedFalse(String id);

    Page<Message> findAllByIsDeletedFalse(Pageable pageable);
}
