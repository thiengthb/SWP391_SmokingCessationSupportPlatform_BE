package com.swpteam.smokingcessation.apis.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Account> findAllByIsDeletedFalse();
}