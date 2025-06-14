package com.swpteam.smokingcessation.feature.service.impl.tracking;

import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.mapper.PhaseMapper;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Phase;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.repository.PhaseRepository;
import com.swpteam.smokingcessation.feature.repository.PlanRepository;
import com.swpteam.smokingcessation.feature.service.interfaces.tracking.PhaseService;
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
public class PhaseServiceImpl implements PhaseService {
    PhaseRepository phaseRepository;
    PhaseMapper phaseMapper;
    PlanRepository planRepository;
    @Override
    public Page<PhaseResponse> getPhasePage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Phase.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Phase> phases = phaseRepository.findAllByIsDeletedFalse(pageable);

        return phases.map(phaseMapper::toResponse);
    }

    @Override
    public PhaseResponse getPhaseById(String id) {
        Phase phase = phaseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PHASE_NOT_FOUND));

        return phaseMapper.toResponse(phase);
    }

    @Override
    @Transactional
    public PhaseResponse createPhase(PhaseRequest request) {
        Phase phase = phaseMapper.toEntity(request);

        Plan plan = planRepository.findByIdAndIsDeletedFalse(request.getPlanId()).
                orElseThrow(()-> new AppException(ErrorCode.PLAN_NOT_FOUND));

        phase.setPlan(plan);

        return phaseMapper.toResponse(phaseRepository.save(phase));
    }

    @Override
    @Transactional
    public PhaseResponse updatePhaseById(String id, PhaseRequest request) {
        Phase phase = phaseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PHASE_NOT_FOUND));

        phaseMapper.update(phase, request);

        return phaseMapper.toResponse(phaseRepository.save(phase));
    }

    @Override
    @Transactional
    public void softDeletePhaseById(String id) {
        Phase phase = phaseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PHASE_NOT_FOUND));

        phase.setDeleted(true);

        phaseRepository.save(phase);
    }
}