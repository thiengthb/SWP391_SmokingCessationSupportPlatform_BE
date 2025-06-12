package com.swpteam.smokingcessation.apis.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>  {
    Optional<Transaction> findByIdAndIsDeletedFalse(String id);

    Page<Transaction> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<Transaction> findAllByIsDeletedFalse(Pageable pageable);
}
