package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.domain.dto.record.RecordCreateRequest;
import com.swpteam.smokingcessation.domain.dto.record.RecordResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Record;
import com.swpteam.smokingcessation.domain.entity.Streak;
import com.swpteam.smokingcessation.domain.mapper.RecordMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.RecordRepository;
import com.swpteam.smokingcessation.repository.StreakRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecordServiceImplTest {

    @Mock AccountRepository accountRepository;
    @Mock RecordRepository recordRepository;
    @Mock StreakRepository streakRepository;
    @Mock RecordMapper recordMapper;

    @InjectMocks RecordServiceImpl recordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRecord_firstRecordOfDay_shouldIncreaseStreak() {
        // Arrange
        String accountId = "acc1";
        LocalDate today = LocalDate.now();
        RecordCreateRequest request = RecordCreateRequest.builder()
                .accountId(accountId)
                .cigarettesSmoked(1)
                .date(today)
                .build();

        Account account = new Account();
        account.setId(accountId);

        Record record = new Record();
        record.setAccount(account);
        record.setDate(today);

        Streak streak = new Streak();
        streak.setStreak(3);

        RecordResponse response = new RecordResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(recordMapper.toEntity(request)).thenReturn(record);
        when(recordRepository.save(record)).thenReturn(record);
        when(recordRepository.countByAccountIdAndDateAndIsDeletedFalse(accountId, today)).thenReturn(1L);
        when(streakRepository.findByMember_Account_Id(accountId)).thenReturn(Optional.of(streak));
        when(streakRepository.save(streak)).thenReturn(streak);
        when(recordMapper.toResponse(record)).thenReturn(response);

        // Act
        RecordResponse result = recordService.createRecord(request);

        // Assert
        assertEquals(response, result);
        assertEquals(4, streak.getStreak());
        verify(streakRepository).save(streak);
    }

    @Test
    void createRecord_notFirstRecordOfDay_shouldNotIncreaseStreak() {
        // Arrange
        String accountId = "acc1";
        LocalDate today = LocalDate.now();
        RecordCreateRequest request = RecordCreateRequest.builder()
                .accountId(accountId)
                .cigarettesSmoked(1)
                .date(today)
                .build();

        Account account = new Account();
        account.setId(accountId);

        Record record = new Record();
        record.setAccount(account);
        record.setDate(today);

        Streak streak = new Streak();
        streak.setStreak(3);

        RecordResponse response = new RecordResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(recordMapper.toEntity(request)).thenReturn(record);
        when(recordRepository.save(record)).thenReturn(record);
        when(recordRepository.countByAccountIdAndDateAndIsDeletedFalse(accountId, today)).thenReturn(2L);
        // streakRepository.findByMember_Account_Id should not be called
        when(recordMapper.toResponse(record)).thenReturn(response);

        // Act
        RecordResponse result = recordService.createRecord(request);

        // Assert
        assertEquals(response, result);
        verify(streakRepository, never()).save(any());
    }

    @Test
    void createRecord_accountNotFound_shouldThrowException() {
        // Arrange
        String accountId = "acc1";
        RecordCreateRequest request = RecordCreateRequest.builder()
                .accountId(accountId)
                .cigarettesSmoked(1)
                .date(LocalDate.now())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppException.class, () -> recordService.createRecord(request));
    }
}