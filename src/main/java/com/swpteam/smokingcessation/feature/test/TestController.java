package com.swpteam.smokingcessation.feature.test;

import com.swpteam.smokingcessation.feature.version1.internalization.MessageSourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
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
}
