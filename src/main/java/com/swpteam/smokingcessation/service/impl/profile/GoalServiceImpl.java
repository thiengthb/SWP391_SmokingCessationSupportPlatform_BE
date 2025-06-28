package com.swpteam.smokingcessation.service.impl.profile;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.goal.GoalCreateRequest;
import com.swpteam.smokingcessation.domain.dto.goal.GoalDetailsResponse;
import com.swpteam.smokingcessation.domain.dto.goal.GoalListItemResponse;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public List<GoalListItemResponse> getPublicGoals() {

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        if (currentAccount.getRole() == Role.ADMIN) {
            List<Goal> goals = goalRepository.findAllByAccountIsNullAndIsDeletedFalse(Sort.by("criteriaType").ascending().and(Sort.by("criteriaValue").ascending()));
            return goals.stream()
                    .map(goalMapper::toAdminResponse)
                    .toList();
        }

        List<Object[]> results = goalRepository.findSortedPublicGoals(currentAccount.getId());

        List<GoalDetailsResponse> goals = results.stream()
                .map(goalMapper::mapRowToGoalDetails)
                .toList();

        return goals.stream()
                .map(goal -> goalMapper.toResponse(goal, currentAccount.getId()))
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    public List<GoalListItemResponse> getMyGoals() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        List<Object[]> results = goalRepository.findSortedPersonalGoals(currentAccount.getId());

        List<GoalDetailsResponse> goals = results.stream()
                .map(goalMapper::mapRowToGoalDetails)
                .toList();

        return goals.stream()
                .map(goal -> goalMapper.toResponse(goal, currentAccount.getId()))
                .toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @Cacheable(value = "GOAL_CACHE", key = "#id")
    public GoalDetailsResponse getMyGoalDetailsById(String id) {
        Goal goal = getGoalByIdOrThrow(id);

        return goalMapper.toResponse(goal);
    }

    @Override
    @Cacheable(value = "GOAL_CACHE", key = "#name")
    public GoalDetailsResponse getGoalByName(String name) {
        return goalMapper.toResponse(findGoalByName(name));
    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CachePut(value = "GOAL_CACHE", key = "#result.getId()")
    @CacheEvict(value = "GOAL_PAGE_CACHE", allEntries = true)
    public GoalDetailsResponse createGoal(GoalCreateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        if (goalRepository.existsByNameAndAccountIdAndIsDeletedFalse(request.name(), currentAccount.getId())) {
            throw new AppException(ErrorCode.GOAL_ALREADY_EXISTS);
        }

        Goal goal = goalMapper.toEntity(request);
        if (currentAccount.getRole() != Role.ADMIN) {
            goal.setAccount(currentAccount);
        }

        Goal savedGoal = goalRepository.save(goal);

        goalProgressService.createGoalProgress(goal, currentAccount);
        goalProgressService.ensureGlobalGoalProgressForAllAccounts();
        return goalMapper.toResponse(savedGoal);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CachePut(value = "GOAL_CACHE", key = "#result.getId()")
    @CacheEvict(value = "GOAL_PAGE_CACHE", allEntries = true)
    public GoalDetailsResponse updateGoal(String name, GoalUpdateRequest request) {
        Goal goal = findGoalByName(name);

        if (goal.getAccount() != null && authUtilService.isAdminOrOwner(goal.getAccount().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
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
            throw new AppException(ErrorCode.UNAUTHORIZED);
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

    @Override
    public Goal getGoalByIdOrThrow(String id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.GOAL_NOT_FOUND));
    }
}
