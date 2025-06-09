package com.swpteam.smokingcessation.apis.record.controller;

import com.swpteam.smokingcessation.apis.record.DTO.request.RecordRequest;
import com.swpteam.smokingcessation.apis.record.DTO.request.RecordUpdate;
import com.swpteam.smokingcessation.apis.record.service.RecordService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordController {
    RecordService recordService;

    @PostMapping
    ResponseEntity<ApiResponse> create(@RequestBody @Valid RecordRequest request) {
        return ResponseEntity.ok(ApiResponse.builder()
                .result(recordService.create(request))
                .build());
    }

    @GetMapping
    ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(ApiResponse.builder()
                .result(recordService.getAll())
                .build());
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.builder()
                .result(recordService.getById(id))
                .build());
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody @Valid RecordUpdate request) {
        return ResponseEntity.ok(ApiResponse.builder()
                .result(recordService.update(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        recordService.delete(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .result("Record deleted")
                .build());
    }
}
