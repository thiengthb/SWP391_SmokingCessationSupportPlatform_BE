package com.swpteam.smokingcessation.feature.version1.identity.controller;

import com.swpteam.smokingcessation.feature.version1.identity.service.IAuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Verification", description = "Manage verification-related operations")
public class VerificationController {

    IAuthenticationService authenticationService;

    @NonFinal
    @Value("${app.frontend-domain}")
    String frontendDomain;

    @NonFinal
    @Value("${app.backend-domain}")
    String backendDomain;

    @GetMapping("/verify")
    String verify(
            @RequestParam("token") String token,
            Model model
    ) {
        model.addAttribute("frontendDomain", frontendDomain);
        return !authenticationService.verifyToActivateAccount(token) ?
                "verification-fail-page"
                :
                "verification-success-page";
    }
}
