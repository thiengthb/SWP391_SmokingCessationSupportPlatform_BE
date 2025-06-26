package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.domain.mapper.PhaseMapper;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.integration.mail.IMailService;
import com.swpteam.smokingcessation.repository.PhaseRepository;
import com.swpteam.smokingcessation.service.impl.notification.PhaseSummaryService;
import com.swpteam.smokingcessation.service.interfaces.profile.IScoreService;
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
    PhaseSummaryService phaseSummaryService;
    IMailService mailService;
    IScoreService scoreService;

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
    public void calculateSuccessRateAndUpdatePhase(Phase phase, List<RecordHabit> allRecords) {
        LocalDate start = phase.getStartDate();
        LocalDate end = phase.getEndDate();
        int maxCigPerDay = phase.getCigaretteBound();
        double successRate;
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
        long successDays = 0;
        long missingDays = 0;
        int totalCigs = 0;

        Map<LocalDate, RecordHabit> recordMap = new HashMap<>();
        for (RecordHabit record : allRecords) {
            recordMap.put(record.getDate(), record);
        }

        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDate = start.plusDays(i);
            RecordHabit record = recordMap.get(currentDate);

            if (record == null) {
                missingDays++;
            } else {
                totalCigs += record.getCigarettesSmoked();
                if (record.getCigarettesSmoked() <= maxCigPerDay) {
                    successDays++;
                }
            }
        }

        long allowedTotalCigs = maxCigPerDay * totalDays;
        boolean failedDueToMissingRecords = missingDays > (totalDays / 2);
        boolean failedDueToCigaretteOveruse = totalCigs > allowedTotalCigs;
        if (allowedTotalCigs == 0) {
            successRate = (totalCigs == 0) ? 100.0 : 0.0;
        } else {
            successRate = ((allowedTotalCigs - totalCigs) * 100.0) / allowedTotalCigs;
        }

        if (failedDueToCigaretteOveruse || failedDueToMissingRecords) {
            phase.setSuccessRate(0.0);
            phase.setPhaseStatus(PhaseStatus.FAILED);
            scoreService.updateScore(phase.getPlan().getAccount().getId(), ScoreRule.PHASE_FAIL);
        } else {
            phase.setSuccessRate(successRate);
            phase.setPhaseStatus(PhaseStatus.SUCCESS);
            scoreService.updateScore(phase.getPlan().getAccount().getId(), ScoreRule.PHASE_SUCCESS);
        }

        log.info("Calculated successRate={} for Phase ID={}, successDays={}, totalDays={}, missingDays={}",
                successRate, phase.getId(), successDays, totalDays, missingDays);

        phaseRepository.save(phase);
        PhaseResponse phaseResponse = phaseMapper.toResponse(phase);
        String userMail = phase.getPlan().getAccount().getEmail();
        mailService.sendPhaseSummary(userMail, phaseResponse);

    }

    @Override
    public List<PhaseResponse> getPhaseListByPlanIdAndStartDate(String planId) {
        List<Phase> phases = phaseRepository.findAllByPlanIdAndIsDeletedFalseOrderByStartDateAsc(planId);
        return phases.stream()
                .map(phaseMapper::toResponse)
                .toList();
    }
}


