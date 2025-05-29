package com.swpteam.smoking_cessation.apis.account.repository;

import com.swpteam.smoking_cessation.apis.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {
}
