package com.swpteam.smokingcessation.apis.setting;

import com.swpteam.smokingcessation.apis.setting.dto.SettingRequest;
import com.swpteam.smokingcessation.apis.setting.dto.SettingResponse;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setting")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SettingController {
    SettingService settingService;

    @PostMapping
    ApiResponse<SettingResponse> createSetting(@RequestBody @Valid SettingRequest request) {
        return ApiResponse.<SettingResponse>builder()
                .result(settingService.createSetting(request))
                .build();
    }

    @PutMapping("/{accountId}")
    ApiResponse<SettingResponse> updateSetting(@PathVariable String accountId, @RequestBody @Valid SettingRequest request) {
        return ApiResponse.<SettingResponse>builder()
                .result(settingService.updateSetting(accountId, request))
                .build();
    }

    @DeleteMapping("/{accountId}")
    ApiResponse<String> createSetting(@PathVariable String accountId) {
        settingService.deleteSetting(accountId);
        return ApiResponse.<String>builder()
                .result("Setting has been deleted")
                .build();
    }

    @GetMapping
    ApiResponse<List<SettingResponse>> getSettingList() {
        return ApiResponse.<List<SettingResponse>>builder()
                .result(settingService.getSettingList())
                .build();
    }

    @GetMapping("/{accountId}")
    ApiResponse<SettingResponse> getSettingList(@PathVariable String accountId) {
        return ApiResponse.<SettingResponse>builder()
                .result(settingService.getSetting(accountId))
                .build();
    }
}
