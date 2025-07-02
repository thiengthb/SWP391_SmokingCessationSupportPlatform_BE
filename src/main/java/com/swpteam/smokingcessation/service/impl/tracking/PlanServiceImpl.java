package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseTemplateResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanTemplateResponse;
import com.swpteam.smokingcessation.domain.dto.tip.TipResponse;
import com.swpteam.smokingcessation.domain.entity.*;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.domain.mapper.PhaseMapper;
import com.swpteam.smokingcessation.domain.mapper.PlanMapper;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.PlanRepository;
import com.swpteam.smokingcessation.service.impl.internalization.MessageSourceService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPlanService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.FileLoaderUtil;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    public PageResponse<PlanResponse> getMyPlanPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Plan.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Plan> plans = planRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(plans.map(planMapper::toResponse));
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

        // check if reach limited plan(2) ? throw error
        restrictPlanLimit(activePlan, pendingPlans);

        // Validate phases in plan
        validatePhaseDates(request.phases());

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
            plan.getPhases().get(i).setPhase(i + 1);
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

       /* Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        Optional<Plan> existingPlan = planRepository.findFirstByAccountIdAndPlanStatusInAndIsDeletedFalse(
                currentAccount.getId(),
                List.of(PlanStatus.ACTIVE, PlanStatus.PENDING)
        );
        if (existingPlan.isPresent()) {
            throw new AppException(ErrorCode.PLAN_ALREADY_EXISTED);
        }

        validatePhaseDates(request.phases());

        Plan plan = planMapper.toEntity(request);

        if (plan.getPhases() != null) {
            plan.getPhases().forEach(phase ->
            {
                phase.setPlan(plan);
                if (phase.getTips() != null) {
                    phase.getTips().forEach(tip -> tip.setPhase(phase));
                }
            });
        }
        plan.getPhases().sort(Comparator.comparing(Phase::getStartDate));

        for (int i = 0; i < plan.getPhases().size(); i++) {
            plan.getPhases().get(i).setPhase(i + 1);
        }

        plan.setAccount(currentAccount);
        plan.setStartDate(plan.getPhases().getFirst().getStartDate());
        plan.setEndDate(plan.getPhases().getLast().getEndDate());

        if (plan.getStartDate().isEqual(LocalDate.now())) {
            plan.setPlanStatus(PlanStatus.ACTIVE);
        } else {
            plan.setPlanStatus(PlanStatus.PENDING);
        }
        return planMapper.toResponse(planRepository.save(plan));
        }
        */


    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @CachePut(value = "PLAN_CACHE", key = "#result.getId()")
    @CacheEvict(value = "PLAN_PAGE_CACHE", allEntries = true)
    public PlanResponse updatePlanById(String planId, PlanRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        Plan plan = planRepository.findByIdAndAccountIdAndIsDeletedFalse(planId, currentAccount.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        // 2. Chỉ cho phép update nếu PENDING
        if (plan.getPlanStatus() != PlanStatus.PENDING) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_PLAN_NOT_PENDING);
        }

        // 3. Validate phase ngày tháng
        validatePhaseDates(request.phases());

        // 4. Xóa toàn bộ phase cũ (nếu phase có quan hệ orphanRemoval=true thì sẽ tự động xóa DB)
        plan.getPhases().clear();

        // 5. Map phase mới từ request
        List<Phase> newPhases = request.phases().stream()
                .map(phaseRequest -> {
                    Phase phase = phaseMapper.toEntity(phaseRequest);
                    phase.setPlan(plan);
                    if (phase.getTips() != null) {
                        phase.getTips().forEach(tip -> tip.setPhase(phase));
                    }
                    return phase;
                })
                .toList();

        // 6. Sắp xếp thứ tự
        newPhases.sort(Comparator.comparing(Phase::getStartDate));
        for (int i = 0; i < newPhases.size(); i++) {
            newPhases.get(i).setPhase(i + 1);
        }

        // 7. Gán lại list phase
        plan.setPhases(newPhases);

        // 8. Cập nhật plan info
        plan.setPlanName(request.planName());
        plan.setDescription(request.description());
        plan.setStartDate(newPhases.getFirst().getStartDate());
        plan.setEndDate(newPhases.getLast().getEndDate());

        // 9. Lưu và trả về response
        return planMapper.toResponse(planRepository.save(plan));
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    @CachePut(value = "PLAN_CACHE", key = "'ALL'")
    public List<PlanResponse> generateAllPlans() {
        // Load all templates
        List<PlanTemplateResponse> templates = FileLoaderUtil.loadPlanTemplate("quitplan/template-plan.json");

        List<PlanResponse> planResponses = new ArrayList<>();

        for (PlanTemplateResponse template : templates) {
            LocalDate planStartDate = LocalDate.now();
            LocalDate currentPhaseStartDate = planStartDate;
            List<PhaseResponse> phases = new ArrayList<>();

            for (PhaseTemplateResponse phase : template.getPlan()) {
                LocalDate phaseEndDate = currentPhaseStartDate.plusDays(phase.getDuration() - 1);

                List<TipResponse> tipResponses = phase.getTips().stream()
                        .map(tipContent -> TipResponse.builder()
                                .content(messageSourceService.getLocalizeMessage(tipContent))
                                .build())
                        .toList();

                PhaseResponse response = PhaseResponse.builder()
                        .phase(phase.getPhase())
                        .phaseName(messageSourceService.getLocalizeMessage(phase.getPhaseName()))
                        .cigaretteBound(phase.getCigaretteBound())
                        .startDate(currentPhaseStartDate)
                        .endDate(phaseEndDate)
                        .description(messageSourceService.getLocalizeMessage(phase.getDescription()))
                        .tips(tipResponses)
                        .build();

                phases.add(response);
                currentPhaseStartDate = phaseEndDate.plusDays(1);
            }

            LocalDate planEndDate = phases.getLast().getEndDate();

            PlanResponse planResponse = PlanResponse.builder()
                    .planName(messageSourceService.getLocalizeMessage(template.getPlanName()))
                    .description(messageSourceService.getLocalizeMessage(template.getDescription()))
                    .startDate(planStartDate)
                    .endDate(planEndDate)
                    .phases(phases)
                    .build();

            planResponses.add(planResponse);
        }

        return planResponses;
    }


    @Override
    @Transactional
    @CacheEvict(value = {"PLAN_CACHE", "PLAN_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void softDeletePlanById(String id) {
        Plan plan = findPlanByIdOrThrowError(id);

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
    public void updateCompletedPlan(Plan plan, double successRate, PlanStatus planStatus) {
        plan.setSuccessRate(successRate);
        plan.setPlanStatus(planStatus);
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

        if (plan.getPlanStatus() != PlanStatus.COMPLETE && plan.getPlanStatus() != PlanStatus.FAILED) {
            throw new AppException(ErrorCode.PLAN_NOT_FOUND);
        }

        return planMapper.toSummaryResponse(plan);
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
}