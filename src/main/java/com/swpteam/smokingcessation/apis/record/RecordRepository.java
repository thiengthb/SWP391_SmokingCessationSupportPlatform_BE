package com.swpteam.smokingcessation.apis.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID> {

    Optional<Record> findByDateAndAccount_Id(LocalDate date, String accountId);
}