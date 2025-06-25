package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Setting;
import com.swpteam.smokingcessation.domain.entity.Streak;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.RecordHabitRepository;
import com.swpteam.smokingcessation.repository.SettingRepository;
import com.swpteam.smokingcessation.repository.StreakRepository;
import com.swpteam.smokingcessation.service.interfaces.tracking.IStreakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreakSchedulerTest {

    @Mock
    SettingRepository settingRepository;
    @Mock
    RecordHabitRepository recordHabitRepository;
    @Mock
    StreakRepository streakRepository;
    @Mock
    IStreakService streakService;

    @InjectMocks
    StreakScheduler streakScheduler;

    Setting setting;
    Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId("acc1");

        setting = new Setting();
        setting.setAccount(account);
        setting.setReportDeadline(LocalTime.of(8, 0));
    }

    @Test
    void checkAndResetStreak_shouldResetStreak_whenNoRecordAndStreakExists() {
        // Arrange
        when(settingRepository.findAllByIsDeletedFalse()).thenReturn(List.of(setting));
        when(recordHabitRepository.existsByAccountIdAndDateAndIsDeletedFalse(anyString(), any(LocalDate.class))).thenReturn(false);

        Streak streak = new Streak();
        streak.setNumber(5);
        when(streakRepository.findByAccountIdAndIsDeletedFalse(anyString())).thenReturn(Optional.of(streak));

        // Simulate time after deadline
        try (MockedStatic<LocalDateTime> localDateTimeMock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

            // Act
            streakScheduler.checkAndResetStreak();

            // Assert
            verify(streakService, times(1)).resetStreak(account.getId());
        }
    }

    @Test
    void checkAndResetStreak_shouldNotResetStreak_whenHasRecord() {
        when(settingRepository.findAllByIsDeletedFalse()).thenReturn(List.of(setting));
        when(recordHabitRepository.existsByAccountIdAndDateAndIsDeletedFalse(anyString(), any(LocalDate.class))).thenReturn(true);

        Streak streak = new Streak();
        streak.setNumber(5);
        when(streakRepository.findByAccountIdAndIsDeletedFalse(anyString())).thenReturn(Optional.of(streak));

        try (MockedStatic<LocalDateTime> localDateTimeMock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

            streakScheduler.checkAndResetStreak();

            verify(streakService, never()).resetStreak(anyString());
        }
    }

    @Test
    void checkAndResetStreak_shouldNotResetStreak_whenStreakIsZero() {
        when(settingRepository.findAllByIsDeletedFalse()).thenReturn(List.of(setting));
        when(recordHabitRepository.existsByAccountIdAndDateAndIsDeletedFalse(anyString(), any(LocalDate.class))).thenReturn(false);

        Streak streak = new Streak();
        streak.setNumber(0);
        when(streakRepository.findByAccountIdAndIsDeletedFalse(anyString())).thenReturn(Optional.of(streak));

        try (MockedStatic<LocalDateTime> localDateTimeMock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

            streakScheduler.checkAndResetStreak();

            verify(streakService, never()).resetStreak(anyString());
        }
    }

    @Test
    void checkAndResetStreak_shouldNotResetStreak_whenBeforeDeadline() {
        when(settingRepository.findAllByIsDeletedFalse()).thenReturn(List.of(setting));

        try (MockedStatic<LocalDateTime> localDateTimeMock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0));
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

            streakScheduler.checkAndResetStreak();

            verify(streakService, never()).resetStreak(anyString());
        }
    }

    @Test
    void checkAndResetStreak_shouldThrowAppException_whenExceptionOccurs() {
        when(settingRepository.findAllByIsDeletedFalse()).thenReturn(List.of(setting));
        when(recordHabitRepository.existsByAccountIdAndDateAndIsDeletedFalse(anyString(), any(LocalDate.class)))
                .thenThrow(new RuntimeException("DB error"));

        try (MockedStatic<LocalDateTime> localDateTimeMock = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

            AppException ex = assertThrows(AppException.class, () -> streakScheduler.checkAndResetStreak());
            assertEquals(ErrorCode.STREAK_RESET_FAILED, ex.getErrorCode());
        }
    }
}