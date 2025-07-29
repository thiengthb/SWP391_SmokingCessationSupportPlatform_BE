package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanPageResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.dto.tip.TipRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.entity.Tip;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.domain.mapper.PhaseMapper;
import com.swpteam.smokingcessation.domain.mapper.PlanMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.internalization.MessageSourceService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IPhaseService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IPlanService;
import com.swpteam.smokingcessation.repository.jpa.PlanRepository;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PlanServiceImpl implements IPlanService {

    PlanMapper planMapper;
    PlanRepository planRepository;
    AuthUtilService authUtilService;
    IPhaseService phaseService;
    MessageSourceService messageSourceService;
    PhaseMapper phaseMapper;

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    @Cacheable(value = "PLAN_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<PlanPageResponse> getMyPlanPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Plan.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Plan> plans = planRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(plans.map(planMapper::toPageResponse));
    }

    @Override
    @Cacheable(value = "PLAN_CACHE", key = "#id")
    public PlanResponse getPlanById(String id) {
        PlanResponse planResponse = planMapper.toResponse(findPlanByIdOrThrowError(id));

        planResponse.setPhases(phaseService.getPhaseListByPlanId(id));

        return planResponse;
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    public PlanResponse getMyCurrentPlan() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Plan plan = planRepository.findFirstByAccountIdAndPlanStatusInAndIsDeletedFalse(currentAccount.getId(),
                        List.of(PlanStatus.ACTIVE, PlanStatus.PENDING))
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        PlanResponse planResponse = planMapper.toResponse(plan);
        planResponse.setProgress(getPlanProgress(plan));
        planResponse.setPhases(phaseService.getPhaseListByPlanIdAndStartDate(plan.getId()));

        return planResponse;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @CachePut(value = "PLAN_CACHE", key = "#result.getId()")
    @CacheEvict(value = "PLAN_PAGE_CACHE", allEntries = true)
    public PlanResponse createPlan(PlanRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        // get Plan with status active and pending
        List<Plan> existingPlans = planRepository.findAllByAccountIdAndPlanStatusInAndIsDeletedFalse(
                currentAccount.getId(),
                List.of(PlanStatus.ACTIVE, PlanStatus.PENDING)
        );

        Optional<Plan> activePlan = existingPlans.stream()
                .filter(p -> p.getPlanStatus() == PlanStatus.ACTIVE)
                .findFirst();

        List<Plan> pendingPlans = existingPlans.stream()
                .filter(p -> p.getPlanStatus() == PlanStatus.PENDING)
                .toList();

        // Validate phases in plan
        validatePhaseDates(request.phases());

        // check if reach limited plan(2) ? throw error
        restrictPlanLimit(activePlan, pendingPlans);

        // Get start and end DATE of new plan
        LocalDate newStartDate = request.phases().stream()
                .map(PhaseRequest::startDate)
                .min(LocalDate::compareTo)
                .get();

        LocalDate newEndDate = request.phases().stream()
                .map(PhaseRequest::endDate)
                .max(LocalDate::compareTo)
                .get();

        //new plan have to be after active || not conflict pending
        restrictPlanDate(newStartDate, newEndDate, activePlan, pendingPlans);

        Plan plan = planMapper.toEntity(request);
        if (plan.getPhases() != null) {
            plan.getPhases().forEach(phase -> {
                phase.setPlan(plan);
                if (phase.getTips() != null) {
                    phase.getTips().forEach(tip -> tip.setPhase(phase));
                }
            });
        }
        plan.getPhases().sort(Comparator.comparing(Phase::getStartDate));
        for (int i = 0; i < plan.getPhases().size(); i++) {
            plan.getPhases().get(i).setPhaseNo(i + 1);
        }

        plan.setAccount(currentAccount);
        plan.setStartDate(newStartDate);
        plan.setEndDate(newEndDate);

        if (plan.getStartDate().isEqual(LocalDate.now())) {
            plan.setPlanStatus(PlanStatus.ACTIVE);
            plan.getPhases().getFirst().setPhaseStatus(PhaseStatus.ACTIVE);
            for (int i = 1; i < plan.getPhases().size(); i++) {
                plan.getPhases().get(i).setPhaseStatus(PhaseStatus.PENDING);
            }
        } else {
            plan.setPlanStatus(PlanStatus.PENDING);
            plan.getPhases().forEach(phase -> {
                phase.setPhaseStatus(PhaseStatus.PENDING);
            });
        }

        return planMapper.toResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @CachePut(value = "PLAN_CACHE", key = "#result.getId()")
    @CacheEvict(value = "PLAN_PAGE_CACHE", allEntries = true)
    public PlanResponse updatePlanById(String planId, PlanRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        Plan plan = planRepository.findByIdAndAccountIdAndIsDeletedFalse(planId, currentAccount.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        //only allow update for pending plan
        if (plan.getPlanStatus() != PlanStatus.PENDING) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_PLAN_NOT_PENDING);
        }

        // 2. Validate phase ngày tháng và sort theo startDate
        validatePhaseDates(request.phases());

        LocalDate[] dateRange = getDateRangeFromPhases(request.phases());
        LocalDate newStartDate = dateRange[0];
        LocalDate newEndDate = dateRange[1];

        validatePlanConflicts(currentAccount.getId(), planId, newStartDate, newEndDate);

        updatePlanPhases(plan, request.phases());
        updatePlanBasicInfo(plan, request);

        updatePlanDatesAndStatus(plan, newStartDate, newEndDate);

        return planMapper.toResponse(planRepository.save(plan));
    }


    private LocalDate[] getDateRangeFromPhases(List<PhaseRequest> phases) {
        LocalDate startDate = phases.stream()
                .map(PhaseRequest::startDate)
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new AppException(ErrorCode.PHASE_REQUIRED));

        LocalDate endDate = phases.stream()
                .map(PhaseRequest::endDate)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new AppException(ErrorCode.PHASE_REQUIRED));

        return new LocalDate[]{startDate, endDate};
    }

    private void validatePlanConflicts(String accountId, String excludePlanId, LocalDate startDate, LocalDate endDate) {
        List<Plan> existingPlans = planRepository.findAllByAccountIdAndPlanStatusInAndIsDeletedFalse(
                accountId, List.of(PlanStatus.ACTIVE, PlanStatus.PENDING));

        Optional<Plan> activePlan = existingPlans.stream()
                .filter(p -> p.getPlanStatus() == PlanStatus.ACTIVE)
                .findFirst();

        List<Plan> pendingPlans = existingPlans.stream()
                .filter(p -> p.getPlanStatus() == PlanStatus.PENDING && !p.getId().equals(excludePlanId))
                .toList();

        restrictPlanDate(startDate, endDate, activePlan, pendingPlans);
    }

    private void updatePlanPhases(Plan plan, List<PhaseRequest> phaseRequests) {
        List<PhaseRequest> sortedPhaseRequests = new ArrayList<>(phaseRequests);
        sortedPhaseRequests.sort(Comparator.comparing(PhaseRequest::startDate));

        mergeAndUpdatePhases(plan, sortedPhaseRequests);
    }

    private void updatePlanBasicInfo(Plan plan, PlanRequest request) {
        if (request.planName() != null && !request.planName().trim().isEmpty()) {
            plan.setPlanName(request.planName());
        }

        if (request.description() != null && !request.description().trim().isEmpty()) {
            plan.setDescription(request.description());
        }
    }

    private void updatePlanDatesAndStatus(Plan plan, LocalDate startDate, LocalDate endDate) {
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);

        LocalDate today = LocalDate.now();
        if (startDate.isEqual(today)) {
            plan.setPlanStatus(PlanStatus.ACTIVE);
            setPhaseStatusForActivePlan(plan);
        } else {
            plan.setPlanStatus(PlanStatus.PENDING);
            plan.getPhases().forEach(phase -> phase.setPhaseStatus(PhaseStatus.PENDING));
        }
    }

    private void setPhaseStatusForActivePlan(Plan plan) {
        List<Phase> phases = plan.getPhases();
        phases.sort(Comparator.comparing(Phase::getPhaseNo));

        if (!phases.isEmpty()) {
            phases.get(0).setPhaseStatus(PhaseStatus.ACTIVE);
            for (int i = 1; i < phases.size(); i++) {
                phases.get(i).setPhaseStatus(PhaseStatus.PENDING);
            }
        }
    }

    private void mergeTips(Phase existingPhase, List<TipRequest> tipRequests) {
        List<Tip> currentTips = existingPhase.getTips();

        int requestSize = tipRequests.size();
        int currentSize = currentTips.size();

        // Merge existing tips by index
        for (int i = 0; i < Math.min(requestSize, currentSize); i++) {
            TipRequest tipRequest = tipRequests.get(i);
            Tip existingTip = currentTips.get(i);

            // Only update non-null/non-empty fields
            if (tipRequest.content() != null && !tipRequest.content().trim().isEmpty()) {
                existingTip.setContent(tipRequest.content());
            }
        }

        // Add new tips if request has more
        if (requestSize > currentSize) {
            for (int i = currentSize; i < requestSize; i++) {
                TipRequest tipRequest = tipRequests.get(i);
                Tip newTip = Tip.builder()
                        .content(tipRequest.content())
                        .phase(existingPhase)
                        .build();

                currentTips.add(newTip);
            }
        }

        // Remove excess tips if request has fewer
        if (requestSize < currentSize) {
            List<Tip> tipsToRemove = new ArrayList<>();
            for (int i = requestSize; i < currentSize; i++) {
                tipsToRemove.add(currentTips.get(i));
            }

            currentTips.removeAll(tipsToRemove);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"PLAN_CACHE", "PLAN_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void softDeletePlanById(String id) {
        Plan plan = findPlanByIdOrThrowError(id);

        plan.setPlanStatus(PlanStatus.CANCELLED);
        plan.setDeleted(true);
        plan.getPhases().forEach(phase -> phase.setDeleted(true));

        plan.setDeleted(true);

        planRepository.save(plan);
    }

    @Override
    @Transactional
    public Plan findPlanByIdOrThrowError(String id) {
        Plan plan = planRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        if (plan.getAccount().isDeleted()) {
            plan.setDeleted(true);
            planRepository.save(plan);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return plan;
    }

    //validate for each phase in Plan
    private void validatePhaseDates(List<PhaseRequest> phases) {
        //if List<phases> null => throw exception
        if (phases == null || phases.isEmpty()) {
            throw new AppException(ErrorCode.PHASE_REQUIRED);
        }

        for (PhaseRequest phase : phases) {
            //ensure that in each phase , phase endDate must be after phase startDate
            if (!phase.endDate().isAfter(phase.startDate())) {
                throw new AppException(ErrorCode.INVALID_PHASE_DATE);
            }
            long days = ChronoUnit.DAYS.between(phase.startDate(), phase.endDate()) + 1;
            //ensue each phase duration must >=7 days
            if (days < 7) {
                throw new AppException(ErrorCode.PHASE_DURATION_TOO_SHORT);
            }
        }

        phases.sort(Comparator.comparing(PhaseRequest::startDate));
        for (int i = 0; i < phases.size() - 1; i++) {
            PhaseRequest current = phases.get(i);
            PhaseRequest next = phases.get(i + 1);
            //check phase 1-2, 2-3, 3-4,...n to ensure that the next phase startDate has to be exactly after currentPhase endDate
            if (!next.startDate().equals(current.endDate().plusDays(1))) {
                throw new AppException(ErrorCode.NEW_PHASE_CONFLICT);
            }
        }

        long totalDays = ChronoUnit.DAYS.between(phases.getFirst().startDate(), phases.getLast().endDate()) + 1;
        //validate that the total amount of a plan have to be >= 14 days
        if (totalDays < 14) {
            throw new AppException(ErrorCode.INVALID_PLAN_DURATION);
        }
    }

    @Override
    public void dailyCheckingPlanStatus() {
        LocalDate today = LocalDate.now();
        List<Plan> pendingPlans = planRepository.findAllByPlanStatusAndIsDeletedFalse(PlanStatus.PENDING);
        if (pendingPlans.isEmpty()) {
            return;
        }
        for (Plan plan : pendingPlans) {
            if (plan.getStartDate().isEqual(today)) {
                plan.setPlanStatus(PlanStatus.ACTIVE);
                plan.getPhases().getFirst().setPhaseStatus(PhaseStatus.ACTIVE);
            }
        }
        planRepository.saveAll(pendingPlans);
    }

    @Override
    public Plan findByAccountIdAndPlanStatusAndIsDeletedFalse(String accountId, PlanStatus planStatus) {
        Plan plan = planRepository.findByAccountIdAndPlanStatusAndIsDeletedFalse(accountId, planStatus)
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        log.info("Found Plan: ID={}, Status={}, AccountID={}, Start={}, End={}",
                plan.getId(),
                plan.getPlanStatus(),
                plan.getAccount().getId(),
                plan.getStartDate(),
                plan.getEndDate()
        );

        return plan;
    }

    @Override
    public void updateCompletedPlan(Plan plan, double successRate, PlanStatus planStatus,
                                    int maxCig, int minCig, long totalReportedDays, long totalNotReportedDays) {
        plan.setSuccessRate(successRate);
        plan.setPlanStatus(planStatus);
        plan.setTotalMostSmoked(maxCig);
        plan.setTotalLeastSmoked(minCig);
        plan.setTotalDaysReported(totalReportedDays);
        plan.setTotalDaysNotReported(totalNotReportedDays);
        planRepository.save(plan);
    }


    @Override
    public List<Plan> getAllActivePlans() {
        return planRepository.findAllByPlanStatusAndIsDeletedFalse(PlanStatus.ACTIVE);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    public PlanSummaryResponse getPlanSummary(String planId) {
        Plan plan = planRepository.findByIdAndIsDeletedFalse(planId)
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        if (plan.getPlanStatus() != PlanStatus.COMPLETED && plan.getPlanStatus() != PlanStatus.FAILED) {
            throw new AppException(ErrorCode.PLAN_NOT_FOUND);
        }

        return planMapper.toSummaryResponse(plan);
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    public PageResponse<PlanPageResponse> searchMyPlansByName(String name, PageableRequest request) {
        ValidationUtil.checkFieldExist(Plan.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        Pageable pageable = PageableRequest.getPageable(request);

        Page<Plan> plans = planRepository
                .findByAccountIdAndPlanNameContainingIgnoreCaseAndIsDeletedFalse(currentAccount.getId(), name, pageable);

        return new PageResponse<>(plans.map(planMapper::toPageResponse));
    }

    private double getPlanProgress(Plan plan) {
        LocalDate now = LocalDate.now();
        LocalDate start = plan.getStartDate();
        LocalDate end = plan.getEndDate();
        double total = ChronoUnit.DAYS.between(start, end) + 1;

        if (now.isBefore(start)) {
            return 0.0;
        }
        if (now.isAfter(end)) {
            return 1.0;
        }
        double passed = ChronoUnit.DAYS.between(start, now) + 1;
        return passed / total;
    }

    private void restrictPlanLimit(Optional<Plan> activePlan, List<Plan> pendingPlans) {
        if (pendingPlans.size() >= 2) {
            throw new AppException(ErrorCode.PLAN_ALREADY_EXISTED_A);
        }
        if (activePlan.isPresent() && !pendingPlans.isEmpty()) {
            throw new AppException(ErrorCode.PLAN_ALREADY_EXISTED_B);
        }
    }

    private void restrictPlanDate(LocalDate newStartDate, LocalDate newEndDate,
                                  Optional<Plan> activePlan, List<Plan> pendingPlans) {
        if (pendingPlans.size() == 1) {
            Plan pending = pendingPlans.getFirst();
            boolean isBefore = newEndDate.isBefore(pending.getStartDate());
            boolean isAfter = newStartDate.isAfter(pending.getEndDate());
            if (!(isBefore || isAfter)) {
                throw new AppException(ErrorCode.RESTRICT_PLAN_A);
            }
        }
        if (activePlan.isPresent()) {
            if (!newStartDate.isAfter(activePlan.get().getEndDate())) {
                throw new AppException(ErrorCode.RESTRICT_PLAN_B);
            }
        }
    }

    private void mergeAndUpdatePhases(
            Plan plan,
            List<PhaseRequest> sortedPhaseRequests
    ) {
        List<Phase> currentPhases = new ArrayList<>(plan.getPhases());
        currentPhases.sort(Comparator.comparing(Phase::getPhaseNo));

        int requestSize = sortedPhaseRequests.size();
        int currentSize = currentPhases.size();

        // Update existing phases
        for (int i = 0; i < Math.min(requestSize, currentSize); i++) {
            PhaseRequest phaseRequest = sortedPhaseRequests.get(i);
            Phase existingPhase = currentPhases.get(i);

            if (phaseRequest.phaseName() != null && !phaseRequest.phaseName().trim().isEmpty()) {
                existingPhase.setPhaseName(phaseRequest.phaseName());
            }
            if (phaseRequest.description() != null && !phaseRequest.description().trim().isEmpty()) {
                existingPhase.setDescription(phaseRequest.description());
            }
            if (phaseRequest.cigaretteBound() != null) {
                existingPhase.setCigaretteBound(phaseRequest.cigaretteBound());
            }
            if (phaseRequest.startDate() != null) {
                existingPhase.setStartDate(phaseRequest.startDate());
            }
            if (phaseRequest.endDate() != null) {
                existingPhase.setEndDate(phaseRequest.endDate());
            }

            // Merge tips
            if (phaseRequest.tips() != null) {
                mergeTips(existingPhase, phaseRequest.tips());
            }
        }

        // Add new phases
        if (requestSize > currentSize) {
            for (int i = currentSize; i < requestSize; i++) {
                PhaseRequest phaseRequest = sortedPhaseRequests.get(i);
                Phase newPhase = phaseMapper.toEntity(phaseRequest);
                newPhase.setPlan(plan);
                newPhase.setPhaseNo(i + 1);
                newPhase.setPhaseStatus(PhaseStatus.PENDING);

                if (newPhase.getTips() != null) {
                    newPhase.getTips().forEach(tip -> tip.setPhase(newPhase));
                }

                plan.getPhases().add(newPhase);
            }
        }

        // Remove excess phases
        if (requestSize < currentSize) {
            List<Phase> phasesToRemove = new ArrayList<>();
            for (int i = requestSize; i < currentSize; i++) {
                phasesToRemove.add(currentPhases.get(i));
            }
            plan.getPhases().removeAll(phasesToRemove);
        }
    }

}