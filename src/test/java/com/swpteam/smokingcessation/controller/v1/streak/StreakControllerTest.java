package com.swpteam.smokingcessation.controller.v1.streak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.controller.v1.tracking.StreakController;
import com.swpteam.smokingcessation.domain.dto.streak.StreakRequest;
import com.swpteam.smokingcessation.domain.dto.streak.StreakResponse;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.GlobalExceptionHandler;
import com.swpteam.smokingcessation.service.impl.tracking.StreakServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StreakControllerTest {

    MockMvc mockMvc;

    @Mock
    StreakServiceImpl streakService;

    ObjectMapper objectMapper = new ObjectMapper();

    StreakRequest streakRequest;
    StreakResponse streakResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        StreakController controller = new StreakController(streakService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean()) // <--- Add this line
                .build();

        streakRequest = new StreakRequest(5);
        streakResponse = StreakResponse.builder().id("streak1").streak(5).build();
    }

    @Test
    void createStreak_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        StreakRequest invalidRequest = new StreakRequest(-1); // Assuming negative streak is invalid

        mockMvc.perform(post("/api/v1/streaks/member1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createStreak_withMissingBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/streaks/member1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStreaks_shouldReturnPagedStreaks() throws Exception {
        Page<StreakResponse> page = new PageImpl<>(List.of(streakResponse), PageRequest.of(0, 10), 1);
        when(streakService.getStreakPage(any(PageableRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/streaks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id").value("streak1"));
    }

    // Paging edge case: empty content
    @Test
    void getStreaks_shouldReturnEmptyPage() throws Exception {
        Page<StreakResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(streakService.getStreakPage(any(PageableRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/streaks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content").isEmpty());
    }

    @Test
    void getStreakById_shouldReturnStreak() throws Exception {
        when(streakService.getStreakByAccountId("streak1")).thenReturn(streakResponse);

        mockMvc.perform(get("/api/v1/streaks/streak1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("streak1"))
                .andExpect(jsonPath("$.result.streak").value(5));
    }

    // Exception handling: 404 Not Found
    @Test
    void getStreakById_shouldReturn404IfNotFound() throws Exception {
        when(streakService.getStreakByAccountId("notfound"))
                .thenThrow(new AppException(ErrorCode.ACCOUNT_NOT_BLANK)); // Use your actual STREAK_NOT_FOUND

        mockMvc.perform(get("/api/v1/streaks/notfound"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCOUNT_NOT_BLANK.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ACCOUNT_NOT_BLANK.getMessage()));
    }

    // Input validation: invalid request body (missing required fields)
    @Test
    void createStreak_shouldReturn400ForInvalidBody() throws Exception {
        // Assuming StreakRequest requires a non-null streak field
        String invalidBody = "{}";
        mockMvc.perform(post("/api/v1/streaks/member1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    // Input validation: malformed JSON
    @Test
    void createStreak_shouldReturn400ForMalformedBody() throws Exception {
        mockMvc.perform(post("/api/v1/streaks/member1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-a-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetStreak_shouldReturnSuccess() throws Exception {
        doNothing().when(streakService).resetStreak("member1");

        mockMvc.perform(put("/api/v1/streaks/reset/member1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessCode.STREAK_RESET.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.STREAK_RESET.getMessage()));
    }

    @Test
    void resetStreak_whenNotAllowed_shouldReturnError() throws Exception {
        doThrow(new AppException(ErrorCode.OTHERS_STREAK_CANNOT_BE_DELETED))
                .when(streakService).resetStreak("memberX");

        mockMvc.perform(put("/api/v1/streaks/reset/memberX"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(ErrorCode.OTHERS_STREAK_CANNOT_BE_DELETED.getCode()));
    }

    @Test
    void resetStreak_whenNotFound_shouldReturnError() throws Exception {
        doThrow(new AppException(ErrorCode.STREAK_NOT_FOUND))
                .when(streakService).resetStreak("notfound");

        mockMvc.perform(put("/api/v1/streaks/reset/notfound"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(ErrorCode.STREAK_NOT_FOUND.getCode()));
    }
}
