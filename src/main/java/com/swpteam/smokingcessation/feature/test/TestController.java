package com.swpteam.smokingcessation.feature.test;

import com.google.api.client.auth.oauth2.Credential;
import com.swpteam.smokingcessation.feature.integration.google.GoogleMeetService;
import com.swpteam.smokingcessation.feature.version1.internalization.MessageSourceService;
import com.swpteam.smokingcessation.utils.GoogleAuthorizeUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/test")
@Tag(name = "Testing", description = "For testing services")
public class TestController {

    Environment environment;
    MessageSourceService messageSourceService;
    private final GoogleMeetService googleMeetService;


    @GetMapping("/ping")
    String ping() {
        String port = environment.getProperty("server.port");
        return "Pinging from server with port: " + port;
    }

    @GetMapping("/language")
    String localizeMessage() {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSourceService.getMessage("welcome.message", null, locale);
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize() {
        try {
            Credential credential = GoogleAuthorizeUtil.authorize();
            return ResponseEntity.ok("Access Token: " + credential.getAccessToken() +
                    "\nRefresh Token: " + credential.getRefreshToken());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/createBooking")
    public ResponseEntity<String> createTestMeet() throws Exception {
        String link = googleMeetService.createGoogleMeet("Test Meeting", "email@gmail.com");
        return ResponseEntity.ok(link);
    }
}
