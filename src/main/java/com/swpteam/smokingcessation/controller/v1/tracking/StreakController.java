package com.swpteam.smokingcessation.controller.v1.tracking;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.streak.StreakResponse;
import com.swpteam.smokingcessation.service.interfaces.tracking.IStreakService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/streaks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Streak", description = "Manage streak-related operations")
public class StreakController {

    IStreakService streakService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<StreakResponse>>> getStreaks(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.RECORD_DELETED,
                streakService.getStreakPage(request)
        );
    }

    @GetMapping("/{memberId}")
    ResponseEntity<ApiResponse<StreakResponse>> getStreakById(
            @PathVariable String memberId
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.RECORD_DELETED,
                streakService.getStreakByAccountId(memberId)
        );
    }

    @PutMapping("/reset/{memberId}")
    ResponseEntity<ApiResponse<Void>> resetStreak(
            @PathVariable String memberId
    ) {
        streakService.resetStreak(memberId);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.STREAK_RESET
        );
    }

}
