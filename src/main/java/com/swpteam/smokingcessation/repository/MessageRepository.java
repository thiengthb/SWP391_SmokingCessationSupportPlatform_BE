package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findAllByIsDeletedFalse();

    Optional<Message> findByIdAndIsDeletedFalse(String id);

    Page<Message> findAllByIsDeletedFalse(Pageable pageable);
}
