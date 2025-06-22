package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.streak.StreakResponse;
import com.swpteam.smokingcessation.service.interfaces.tracking.IStreakService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/streaks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Streak", description = "Manage streak-related operations")
public class StreakController {

    IStreakService streakService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<StreakResponse>>> getStreaks(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_DELETED,
                streakService.getStreakPage(request)
        );
    }

    @GetMapping("/{memberId}")
    ResponseEntity<ApiResponse<StreakResponse>> getStreakById(
            @PathVariable String memberId
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.RECORD_DELETED,
                streakService.getStreakByAccountId(memberId)
        );
    }

    @PutMapping("/reset/{memberId}")
    ResponseEntity<ApiResponse<Void>> resetStreak(
            @PathVariable String memberId
    ) {
        streakService.resetStreak(memberId);
        return ResponseUtil.buildResponse(
                SuccessCode.STREAK_RESET,
                null
        );
    }

}
