package com.swpteam.smokingcessation.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomUtil {

    private final Random random = new Random();

    public String generateRandomUsername() {
        String prefix = "User";
        int randomNumber = random.nextInt(900000) + 100000;
        return prefix + randomNumber;
    }

    public String generateVerificationCode() {
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

}
