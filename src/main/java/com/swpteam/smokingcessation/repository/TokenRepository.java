package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Token;
import com.swpteam.smokingcessation.domain.enums.TokenType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    boolean existsById(@NotNull String id);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Token t WHERE t.id = :id AND t.expiryTime > :now")
    boolean existsByIdAndNotExpired(@Param("id") String id, @Param("now") LocalDateTime now);

    int deleteAllByExpiryTimeBefore(LocalDateTime now);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.account.id = :accountId AND t.tokenType = :tokenType")
    void deleteByAccountIdAndTokenType(@Param("accountId") String accountId, @Param("tokenType") TokenType tokenType);
}
