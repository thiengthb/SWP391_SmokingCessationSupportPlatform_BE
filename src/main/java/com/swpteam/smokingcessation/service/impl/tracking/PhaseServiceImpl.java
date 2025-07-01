package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.domain.mapper.PhaseMapper;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.integration.mail.IMailService;
import com.swpteam.smokingcessation.repository.PhaseRepository;
import com.swpteam.smokingcessation.service.interfaces.notification.INotificationService;
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
    IScoreService scoreService;
    INotificationService notificationService;
    IMailService mailService;

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
        String accountId=phase.getPlan().getAccount().getId();

        Map<LocalDate, RecordHabit> recordMap = new HashMap<>();
        for (RecordHabit record : allRecords) {
            recordMap.put(record.getDate(), record);
        }

        int maxSmoked = 0; // ADDED
        int minSmoked = Integer.MAX_VALUE;

        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDate = start.plusDays(i);
            RecordHabit record = recordMap.get(currentDate);

            if (record == null) {
                missingDays++;
            } else {
                int cigs = record.getCigarettesSmoked();
                totalCigs += record.getCigarettesSmoked();

                if(cigs > maxSmoked){
                    maxSmoked = cigs;
                }

                if (cigs < minSmoked) {
                    minSmoked = cigs;
                }

                if (record.getCigarettesSmoked() <= maxCigPerDay) {
                    successDays++;
                }
            }
        }
        if(isPhaseFullyReported(totalDays,allRecords)){
            log.info("report all phase award:");
            scoreService.updateScore(accountId,ScoreRule.REPORTED_ALL_PHASE);
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
            log.info("phase fail minus");
            scoreService.updateScore(accountId, ScoreRule.PHASE_FAIL);
        } else {
            phase.setSuccessRate(successRate);
            phase.setPhaseStatus(PhaseStatus.SUCCESS);
            log.info("phase succes award:");
            scoreService.updateScore(accountId, ScoreRule.PHASE_SUCCESS);
        }

        log.info("Calculated successRate={} for Phase ID={}, successDays={}, totalDays={}, missingDays={}",
                successRate, phase.getId(), successDays, totalDays, missingDays);


        phase.setTotalDaysNotReported(missingDays);
        phase.setTotalDaysReported(totalDays-missingDays);
        if (missingDays == totalDays) {
            phase.setMostSmokeCig(0);
            phase.setLeastSmokeCig(0);
        } else {
            phase.setMostSmokeCig(maxSmoked);
            phase.setLeastSmokeCig(minSmoked);
        }

        phaseRepository.save(phase);
        notificationService.sendPhaseDoneNotification(phase.getPhase(),accountId);
        if (phase.getPlan().getAccount().getStatus() == AccountStatus.ONLINE) {
            notificationService.sendPhaseDoneNotification(phase.getPhase(), accountId);
        }else {
            mailService.sendPhaseSummary(
                    phase.getPlan().getPlanName(),
                    phase.getPlan().getStartDate(),
                    phase.getPlan().getEndDate(),
                    totalDays - missingDays,
                    missingDays,
                    maxSmoked,
                    successRate,
                    phase.getPhaseStatus(),
                    accountId);
        }


    }

    @Override
    public List<PhaseResponse> getPhaseListByPlanIdAndStartDate(String planId) {
        List<Phase> phases = phaseRepository.findAllByPlanIdAndIsDeletedFalseOrderByStartDateAsc(planId);
        return phases.stream()
                .map(phaseMapper::toResponse)
                .toList();
    }
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @Override
    public List<PhaseSummaryResponse> getCompletedPhaseSummaries(String planId) {
        List<Phase> phases = phaseRepository.findByPlanIdAndIsDeletedFalse(planId);

        // Filter phase có status khác null
        List<Phase> completedPhases = phases.stream()
                .filter(p -> p.getPhaseStatus() != null)
                .toList();

        if (completedPhases.isEmpty()) {
            throw new AppException(ErrorCode.NO_COMPLETED_PHASE_FOUND);
        }

        return completedPhases.stream()
                .map(phaseMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public boolean isPhaseFullyReported(Long totalDays, List<RecordHabit> recordHabits) {
        return !recordHabits.isEmpty() && recordHabits.size() == totalDays;

    }
}



