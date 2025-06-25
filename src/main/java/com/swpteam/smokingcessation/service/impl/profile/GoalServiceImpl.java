package com.swpteam.smokingcessation.service.impl.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.goal.GoalCreateRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Goal;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.domain.mapper.GoalMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.GoalRepository;
import com.swpteam.smokingcessation.service.interfaces.profile.IGoalProgressService;
import com.swpteam.smokingcessation.service.interfaces.profile.IGoalService;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GoalServiceImpl implements IGoalService {

    GoalRepository goalRepository;
    GoalMapper goalMapper;
    AuthUtilService authUtilService;
    IGoalProgressService goalProgressService;

    @Override
    @Cacheable(value = "GOAL_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<GoalResponse> getPublicGoalPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Goal.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Goal> goals = goalRepository.findAllByAccountIsNullAndIsDeletedFalse(pageable);

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        if (currentAccount.getRole() == Role.ADMIN) {
            return new PageResponse<>(goals.map(goalMapper::toAdminResponse));
        }

        return new PageResponse<>(goals.map(goal -> goalMapper.toResponse(goal, currentAccount.getId())));
    }

    @Override
    @Cacheable(value = "GOAL_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + T(com.swpteam.smokingcessation.utils.AuthUtilService).getCurrentAccountOrThrowError().getId()")
    public PageResponse<GoalResponse> getMyGoalPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Goal.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Goal> goals = goalRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(goals.map(goal -> goalMapper.toResponse(goal, currentAccount.getId())));
    }

    @Override
    @Cacheable(value = "GOAL_CACHE", key = "#name")
    public GoalResponse getGoalByName(String name) {
        return goalMapper.toResponse(findGoalByName(name));
    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CachePut(value = "GOAL_CACHE", key = "#result.getId()")
    @CacheEvict(value = "GOAL_PAGE_CACHE", allEntries = true)
    public GoalResponse createGoal(GoalCreateRequest request) {
        if (goalRepository.existsByNameAndIsDeletedFalse(request.name())) {
            throw new AppException(ErrorCode.GOAL_ALREADY_EXISTS);
        }

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Goal goal = goalMapper.toEntity(request);
        if (currentAccount.getRole() != Role.ADMIN) {
            goal.setAccount(currentAccount);
        }

        Goal savedGoal = goalRepository.save(goal);

        goalProgressService.createGoalProgress(goal, currentAccount);

        return goalMapper.toResponse(savedGoal);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CachePut(value = "GOAL_CACHE", key = "#result.getId()")
    @CacheEvict(value = "GOAL_PAGE_CACHE", allEntries = true)
    public GoalResponse updateGoal(String name, GoalUpdateRequest request) {
        Goal goal = findGoalByName(name);

        if (goal.getAccount() != null && authUtilService.isAdminOrOwner(goal.getAccount().getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        goalMapper.update(goal, request);

        return goalMapper.toResponse(goalRepository.save(goal));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CacheEvict(value = {"GOAL_CACHE", "GOAL_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void softDeleteGoal(String name) {
        Goal goal = findGoalByName(name);

        if (goal.getAccount() != null && authUtilService.isAdminOrOwner(goal.getAccount().getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        goal.setDeleted(true);

        goalRepository.save(goal);
    }

    @Override
    @Transactional
    public Goal findGoalByName(String name) {
        Goal goal = goalRepository.findByNameAndIsDeletedFalse(name)
                .orElseThrow(() -> new AppException(ErrorCode.GOAL_NOT_FOUND));

        if (goal.getAccount().isDeleted()) {
            throw new AppException(ErrorCode.GOAL_NOT_FOUND);
        }

        return goal;
    }

}
