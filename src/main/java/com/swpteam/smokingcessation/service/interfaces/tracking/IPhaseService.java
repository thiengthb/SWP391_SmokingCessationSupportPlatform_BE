package com.swpteam.smokingcessation.service.interfaces.tracking;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.entity.RecordHabit;

import java.time.LocalDate;
import java.util.List;

public interface IPhaseService {

    List<PhaseResponse> getPhaseListByPlanId(String planId);

    PhaseResponse getPhaseById(String id);

    void softDeletePhaseById(String id);

    Phase findPhaseByIdOrThrowError(String id);

    void calculateSuccessRateAndUpdatePhase(Phase phase, List<RecordHabit> allRecords);

    List<PhaseResponse> getPhaseListByPlanIdAndStartDate(String planId);

    boolean isPhaseFullyReported(Long totalDays, List<RecordHabit> recordHabits);
}