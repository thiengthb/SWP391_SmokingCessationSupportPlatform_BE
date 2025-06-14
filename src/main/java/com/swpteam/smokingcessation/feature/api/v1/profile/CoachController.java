package com.swpteam.smokingcessation.feature.api.v1.profile;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.coach.CoachRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import com.swpteam.smokingcessation.feature.service.impl.profile.CoachServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CoachController {
    CoachServiceImpl coachServiceImpl;

    @GetMapping
    ResponseEntity<ApiResponse<Page<CoachResponse>>> getCoachPage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<CoachResponse>>builder()
                        .code(SuccessCode.COACH_GET_ALL.getCode())
                        .message(SuccessCode.COACH_GET_ALL.getMessage())
                        .result(coachServiceImpl.getCoachPage(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CoachResponse>> getCoachById(@PathVariable String id) {
        CoachResponse response = coachServiceImpl.getCoachById(id);
        return ResponseEntity.ok(
                ApiResponse.<CoachResponse>builder()
                        .code(SuccessCode.COACH_GET_BY_ID.getCode())
                        .message(SuccessCode.COACH_GET_BY_ID.getMessage())
                        .result(response)
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<CoachResponse>> createCoach(@Valid @RequestBody CoachRequest request) {
        CoachResponse response = coachServiceImpl.createCoach(request);
        return ResponseEntity.ok(
                ApiResponse.<CoachResponse>builder()
                        .code(SuccessCode.COACH_CREATED.getCode())
                        .message(SuccessCode.COACH_CREATED.getMessage())
                        .result(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CoachResponse>> updateCoachById(@PathVariable String id, @Valid @RequestBody CoachRequest request) {
        CoachResponse response = coachServiceImpl.updateCoachById(id, request);
        return ResponseEntity.ok(
                ApiResponse.<CoachResponse>builder()
                        .code(SuccessCode.COACH_UPDATED.getCode())
                        .message(SuccessCode.COACH_UPDATED.getMessage())
                        .result(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteCoachById(@PathVariable String id) {
        coachServiceImpl.softDeleteCoachById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(SuccessCode.COACH_DELETED.getCode())
                        .message(SuccessCode.COACH_DELETED.getMessage())
                        .build()
        );
    }
}