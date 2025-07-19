package com.swpteam.smokingcessation.domain.dto.phase;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.tip.TipRequest;
import com.swpteam.smokingcessation.exception.AppException;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PhaseRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Set<ConstraintViolation<PhaseRequest>> validate(PhaseRequest request) {
        return validator.validate(request);
    }

    // TC‑01: Valid data with all fields filled properly
    @Test
    void TC01_validRequest_shouldPass() {
        PhaseRequest request = new PhaseRequest(
                "Phase 1",
                "Short desc",
                0,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                List.of(new TipRequest("Content"))
        );
        assertTrue(validate(request).isEmpty());
    }

    // TC-02: Null phaseName and description are allowed
    @Test
    void TC02_nullNameAndDesc_shouldPass() {
        PhaseRequest request = new PhaseRequest(
                null,
                null,
                0,
                LocalDate.now().plusMonths(2),
                LocalDate.now().plusDays(7),
                null
        );
        assertTrue(validate(request).isEmpty());
    }

    // TC‑03: Phase name exceeds 50 characters
    @Test
    void TC03_phaseNameTooLong_shouldFail() {
        String longName = "A".repeat(51);
        PhaseRequest request = new PhaseRequest(
                longName,
                "Short desc",
                5,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null
        );
        Set<ConstraintViolation<PhaseRequest>> violations = validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("PHASE_NAME_TOO_LONG")));
    }

    // TC‑04: Description exceeds 255 characters
    @Test
    void TC04_descriptionTooLong_shouldFail() {
        String longDesc = "D".repeat(256);
        PhaseRequest request = new PhaseRequest(
                "Phase 1",
                longDesc,
                5,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null
        );
        Set<ConstraintViolation<PhaseRequest>> violations = validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("PHASE_DESCRIPTION_TOO_LONG")));
    }

    // TC‑05: Negative cigarette bound
    @Test
    void TC05_negativeCigaretteBound_shouldFail() {
        PhaseRequest request = new PhaseRequest(
                "Phase 1",
                "Short desc",
                -1,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null
        );
        Set<ConstraintViolation<PhaseRequest>> violations = validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("CIGARETTE_BOUND_INVALID")));
    }

    // TC‑06: Start date is in the past
    @Test
    void TC06_startDateInPast_shouldFail() {
        PhaseRequest request = new PhaseRequest(
                "Phase 1",
                "Short desc",
                5,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(7),
                null
        );
        Set<ConstraintViolation<PhaseRequest>> violations = validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("startDate")));
    }

    // TC‑07: End date is before required min duration (This is handled in the service layer -> PhaseServiceImplTest)

    // TC‑08: Null cigarette bound
    @Test
    void TC08_nullCigaretteBound_shouldFail() {
        PhaseRequest request = new PhaseRequest(
                "Phase 1",
                "Short desc",
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null
        );
        Set<ConstraintViolation<PhaseRequest>> violations = validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("PHASE_CIGARETTE_BOUND_REQUIRED")));
    }
}
