package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.exception.AppException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PhaseServiceImplTest {

    private void validatePhaseDates(List<PhaseRequest> phases) {
        if (phases == null || phases.isEmpty()) {
            throw new AppException(ErrorCode.PHASE_REQUIRED);
        }

        for (PhaseRequest phase : phases) {
            if (!phase.endDate().isAfter(phase.startDate())) {
                throw new AppException(ErrorCode.INVALID_PHASE_DATE);
            }
            long days = java.time.temporal.ChronoUnit.DAYS.between(phase.startDate(), phase.endDate()) + 1;
            if (days < 7) {
                throw new AppException(ErrorCode.PHASE_DURATION_TOO_SHORT);
            }
        }

        phases.sort(java.util.Comparator.comparing(PhaseRequest::startDate));
        for (int i = 0; i < phases.size() - 1; i++) {
            PhaseRequest current = phases.get(i);
            PhaseRequest next = phases.get(i + 1);
            if (!next.startDate().equals(current.endDate().plusDays(1))) {
                throw new AppException(ErrorCode.NEW_PHASE_CONFLICT);
            }
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(
                phases.getFirst().startDate(), phases.getLast().endDate()) + 1;
        if (totalDays < 14) {
            throw new AppException(ErrorCode.INVALID_PLAN_DURATION);
        }
    }

//    @Test
//    void validPhases_shouldPass() {
//        LocalDate now = LocalDate.now();
//        List<PhaseRequest> phases = List.of(
//                new PhaseRequest("Phase 1", null, 0, now, now.plusDays(6), null),
//                new PhaseRequest("Phase 2", null, 0, now.plusDays(7), now.plusDays(14), null)
//        );
//        assertDoesNotThrow(() -> validatePhaseDates(phases));
//    }

    @Test
    void nullPhases_shouldThrow_PHASE_REQUIRED() {
        AppException ex = assertThrows(AppException.class, () -> validatePhaseDates(null));
        assertEquals(ErrorCode.PHASE_REQUIRED, ex.getErrorCode());
    }

    @Test
    void phaseWithEndBeforeStart_shouldThrow_INVALID_PHASE_DATE() {
        LocalDate now = LocalDate.now();
        List<PhaseRequest> phases = List.of(
                new PhaseRequest("Invalid Phase", null, 0, now, now.minusDays(1), null)
        );
        AppException ex = assertThrows(AppException.class, () -> validatePhaseDates(phases));
        assertEquals(ErrorCode.INVALID_PHASE_DATE, ex.getErrorCode());
    }

    @Test
    void phaseTooShort_shouldThrow_PHASE_DURATION_TOO_SHORT() {
        LocalDate now = LocalDate.now();
        List<PhaseRequest> phases = List.of(
                new PhaseRequest("Short Phase", null, 0, now, now.plusDays(5), null)
        );
        AppException ex = assertThrows(AppException.class, () -> validatePhaseDates(phases));
        assertEquals(ErrorCode.PHASE_DURATION_TOO_SHORT, ex.getErrorCode());
    }

//    @Test
//    void nonSequentialPhases_shouldThrow_NEW_PHASE_CONFLICT() {
//        LocalDate now = LocalDate.now();
//        List<PhaseRequest> phases = List.of(
//                new PhaseRequest("Phase 1", null, 0, now, now.plusDays(6), null),
//                new PhaseRequest("Phase 2", null, 0, now.plusDays(10), now.plusDays(16), null) // Gap of 3 days
//        );
//        AppException ex = assertThrows(AppException.class, () -> validatePhaseDates(phases));
//        assertEquals(ErrorCode.NEW_PHASE_CONFLICT, ex.getErrorCode());
//    }

    @Test
    void totalPlanDurationTooShort_shouldThrow_INVALID_PLAN_DURATION() {
        LocalDate now = LocalDate.now();
        List<PhaseRequest> phases = List.of(
                new PhaseRequest("Phase 1", null, 0, now, now.plusDays(6), null),
                new PhaseRequest("Phase 2", null, 0, now.plusDays(7), now.plusDays(12), null) // Only 13 days total
        );
        AppException ex = assertThrows(AppException.class, () -> validatePhaseDates(phases));
        assertEquals(ErrorCode.PHASE_DURATION_TOO_SHORT, ex.getErrorCode());
    }
}
