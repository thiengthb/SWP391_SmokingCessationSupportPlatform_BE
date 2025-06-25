package com.swpteam.smokingcessation.controller.test;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/test")
@Tag(name = "Testing", description = "For testing services")
public class TestController {

    Environment environment;

    @GetMapping("/ping")
    String ping() {
        String port = environment.getProperty("server.port");
        return "Pinging from server with port: " + port;
    }
}
