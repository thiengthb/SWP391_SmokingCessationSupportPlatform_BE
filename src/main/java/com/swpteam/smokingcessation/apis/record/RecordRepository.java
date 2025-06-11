package com.swpteam.smokingcessation.apis.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, String> {

    Optional<Record> findByIdAndIsDeletedFalse(String id);

    Page<Record> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<Record> findAllByIsDeletedFalse(Pageable pageable);
}