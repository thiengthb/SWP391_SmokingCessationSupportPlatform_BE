package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.mapper.PlanMapper;
import com.swpteam.smokingcessation.domain.dto.plan.PlanRequest;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.repository.AccountRepository;
import com.swpteam.smokingcessation.feature.repository.PlanRepository;
import com.swpteam.smokingcessation.feature.service.interfaces.tracking.PlanService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    PlanRepository planRepository;
    PlanMapper planMapper;
    AccountRepository accountRepository;

    @Override
    public Page<PlanResponse> getPlanPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Plan.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Plan> plans = planRepository.findAllByIsDeletedFalse(pageable);

        return plans.map(planMapper::toResponse);
    }

    @Override
    public PlanResponse getPlanById(String id) {
        Plan plan = planRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        return planMapper.toResponse(plan);
    }

    @Override
    @Transactional
    public PlanResponse createPlan(PlanRequest request) {
        Plan plan = planMapper.toEntity(request);

        Account account = accountRepository.findByIdAndIsDeletedFalse(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        plan.setAccount(account);

        return planMapper.toResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    public PlanResponse updatePlanById(String id, PlanRequest request) {
        Plan plan = planRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));
        planMapper.update(plan, request);

        return planMapper.toResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    public void softDeletePlanById(String id) {
        Plan plan = planRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));

        plan.setDeleted(true);

        planRepository.save(plan);
    }
}