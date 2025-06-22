package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.mapper.PhaseMapper;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.PhaseRepository;
import com.swpteam.smokingcessation.service.interfaces.tracking.IPhaseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PhaseServiceImpl implements IPhaseService {

    PhaseMapper phaseMapper;
    PhaseRepository phaseRepository;

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

    private void validateNoOverlap(Plan plan, LocalDate startDate, LocalDate endDate) {
        List<Phase> overlappingPhases = phaseRepository.findOverlappingPhases(
                plan.getId(), startDate, endDate
        );

        if (!overlappingPhases.isEmpty()) {
            throw new AppException(ErrorCode.PHASE_OVERLAP);
        }
    }

}