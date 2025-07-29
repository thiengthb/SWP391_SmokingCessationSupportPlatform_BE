package com.swpteam.smokingcessation.feature.version1.internalization;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.constant.SuccessCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageSourceService {

    MessageSource messageSource;

    public String getLocalizeMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(key, locale);
    }

    public String getMessage(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, key, locale);
    }

    public String getErrorLocalizeMessage(ErrorCode errorCode) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(errorCode.getMessageLocaleKey(), locale);
    }

    public String getSuccessLocalizeMessage(SuccessCode successCode) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(successCode.getMessageLocaleKey(), locale);
    }
}