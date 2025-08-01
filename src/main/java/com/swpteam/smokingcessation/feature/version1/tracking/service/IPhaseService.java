package com.swpteam.smokingcessation.feature.version1.tracking.service;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;

import java.util.List;

public interface IPhaseService {

    List<PhaseResponse> getPhaseListByPlanId(String planId);

    PhaseResponse getPhaseById(String id);

    void softDeletePhaseById(String id);

    Phase findPhaseByIdOrThrowError(String id);

    void calculateSuccessRateAndUpdatePhase(Phase phase, List<RecordHabit> allRecords);

    List<PhaseResponse> getPhaseListByPlanIdAndStartDate(String planId);

    List<PhaseSummaryResponse> getCompletedPhaseSummaries(String planId);

    boolean isPhaseFullyReported(Long totalDays, List<RecordHabit> recordHabits);

    void dailyCheckingPhaseStatus();
}