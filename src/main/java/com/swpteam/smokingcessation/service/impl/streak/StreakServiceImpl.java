package com.swpteam.smokingcessation.service.impl.streak;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.streak.StreakResponse;
import com.swpteam.smokingcessation.domain.dto.streak.StreakRequest;
import com.swpteam.smokingcessation.domain.entity.Streak;
import com.swpteam.smokingcessation.domain.mapper.StreakMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.StreakRepository;
import com.swpteam.smokingcessation.service.interfaces.streak.IStreakService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public class StreakServiceImpl implements IStreakService {
    StreakMapper streakMapper;
    StreakRepository streakRepository;

    @Override
    public StreakResponse createStreak(String id, StreakRequest request) {
        Streak streak = streakMapper.toEntity(request);
        return streakMapper.toResponse(streakRepository.save(streak));
    }

    @Override
    public StreakResponse updateStreak(String id, StreakRequest request) {
        Streak streak = streakRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STREAK_NOT_FOUND));
        streakMapper.update(streak, request);
        return streakMapper.toResponse(streakRepository.save(streak));
    }

    @Override
    public void deleteStreak(String id) {
        Streak streak = streakRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STREAK_NOT_FOUND));
        streakRepository.delete(streak);
    }

    @Override
    public StreakResponse getStreakById(String id) {
        Streak streak = streakRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STREAK_NOT_FOUND));
        return streakMapper.toResponse(streak);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<StreakResponse> getStreakPage(PageableRequest request) {
        Pageable pageable = PageableRequest.getPageable(request);
        return streakRepository.findAll(pageable).map(streakMapper::toResponse);
    }
}
