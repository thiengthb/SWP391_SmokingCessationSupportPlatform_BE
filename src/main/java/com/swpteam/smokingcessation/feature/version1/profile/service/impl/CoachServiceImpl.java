package com.swpteam.smokingcessation.feature.version1.profile.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachCreateRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import com.swpteam.smokingcessation.domain.dto.coach.CoachUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Coach;
import com.swpteam.smokingcessation.domain.mapper.CoachMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.repository.jpa.CoachRepository;
import com.swpteam.smokingcessation.feature.version1.profile.service.ICoachService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CoachServiceImpl implements ICoachService {

    CoachRepository coachRepository;
    CoachMapper coachMapper;
    AuthUtilService authUtilService;
    IAccountService accountService;

    @Override
    public PageResponse<CoachResponse> getCoachPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Coach.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Coach> coaches = coachRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(coaches.map(coachMapper::toResponse));
    }

    @Override
    public CoachResponse getCoachById(String id) {
        return coachMapper.toResponse(findCoachById(id));
    }

    @Override
    @PreAuthorize("hasRole('COACH)")
    public CoachResponse getMyCoachProfile() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();
        return coachMapper.toResponse(findCoachById(currentAccount.getId()));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COACH')")
    public CoachResponse registerCoachProfile(CoachCreateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        if (coachRepository.existsById(currentAccount.getId())) {
            throw new AppException(ErrorCode.COACH_ALREADY_EXISTS);
        }

        Coach coach = coachMapper.toEntity(request);
        coach.setAccount(currentAccount);

        return coachMapper.toResponse(coachRepository.save(coach));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('COACH)")
    public CoachResponse updateMyCoachProfile(CoachUpdateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Coach coach = findCoachById(currentAccount.getId());

        coachMapper.update(coach, request);

        accountService.updateAccountWithoutRole(coach.getAccount().getId(),
                new AccountUpdateRequest(
                        request.email(),
                        null,
                        request.phoneNumber()
                )
        );

        return coachMapper.toResponse(coachRepository.save(coach));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public CoachResponse updateCoachById(String id, CoachUpdateRequest request) {
        Coach coach = findCoachById(id);

        coachMapper.update(coach, request);

        accountService.updateAccountWithoutRole(coach.getAccount().getId(),
                new AccountUpdateRequest(
                        request.email(),
                        null,
                        request.phoneNumber()
                )
        );

        return coachMapper.toResponse(coachRepository.save(coach));
    }

    @Override
    public Coach findCoachById(String id) {
        Coach coach = coachRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.COACH_NOT_FOUND));

        if (coach.getAccount().isDeleted()) {
            coach.setDeleted(true);
            coachRepository.save(coach);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return coach;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<CoachResponse> searchCoachesByName(String name, PageableRequest request) {
        ValidationUtil.checkFieldExist(Coach.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Coach> coaches = coachRepository.findByFullNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable);

        return new PageResponse<>(coaches.map(coachMapper::toResponse));
    }

}