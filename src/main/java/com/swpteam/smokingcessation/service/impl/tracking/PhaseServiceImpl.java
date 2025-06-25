package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.mapper.PhaseMapper;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.PhaseRepository;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PhaseServiceImpl implements IPhaseService {

    PhaseMapper phaseMapper;
    PhaseRepository phaseRepository;

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    @Cacheable(value = "PHASE_CACHE", key = "#planId")
    public List<PhaseResponse> getPhaseListByPlanId(String planId) {
        return phaseRepository.findAllByPlanId(planId)
                .stream().map(phaseMapper::toResponse).toList();
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    @Cacheable(value = "PHASE_CACHE", key = "#id")
    public PhaseResponse getPhaseById(String id) {
        return phaseMapper.toResponse(findPhaseByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @CacheEvict(value = {"PHASE_CACHE", "PHASE_LIST_CACHE"}, key = "#id", allEntries = true)
    public void softDeletePhaseById(String id) {
        Phase phase = findPhaseByIdOrThrowError(id);

        phase.setDeleted(true);

        phaseRepository.save(phase);
    }

    @Override
    @Transactional
    public Phase findPhaseByIdOrThrowError(String id) {
        Phase phase = phaseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PHASE_NOT_FOUND));

        if (phase.getPlan().isDeleted()) {
            phase.setDeleted(true);
            phaseRepository.save(phase);
            throw new AppException(ErrorCode.PLAN_NOT_FOUND);
        }

        return phase;
    }

    @Override
    public double calculateSuccessRateAndUpdatePhase(Phase phase, List<RecordHabit> allRecords) {
        LocalDate start = phase.getStartDate();
        LocalDate end = phase.getEndDate();
        int maxCigs = phase.getCigaretteBound();

        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
        long successDays = 0;
        long missingDays = 0;

        Map<LocalDate, RecordHabit> recordMap = new HashMap<>();
        for (RecordHabit record : allRecords) {
            LocalDate date = record.getDate();
            if (!record.isDeleted() && !date.isBefore(start) && !date.isAfter(end)) {
                recordMap.put(date, record);
            }
        }

        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDate = start.plusDays(i);
            RecordHabit record = recordMap.get(currentDate);

            if (record == null) {
                missingDays++;
            } else if (record.getCigarettesSmoked() <= maxCigs) {
                successDays++;
            }
        }

        long validDays = totalDays - missingDays;
        if (validDays == 0) {
            phase.setSuccessRate(0.0);
            phase.setPhaseStatus(PhaseStatus.FAILED);
            return 0.0;
        }

        double successRate = (successDays * 100.0) / validDays;

        phase.setSuccessRate(successRate);

        if (successRate >= 50.0) {
            phase.setPhaseStatus(PhaseStatus.SUCCESS);
        } else {
            phase.setPhaseStatus(PhaseStatus.FAILED);
        }

        log.info("Calculated successRate={} for Phase ID={}, successDays={}, totalDays={}, missingDays={}",
                successRate, phase.getId(), successDays, totalDays, missingDays);

        phaseRepository.save(phase);
        return successRate;
    }
}


