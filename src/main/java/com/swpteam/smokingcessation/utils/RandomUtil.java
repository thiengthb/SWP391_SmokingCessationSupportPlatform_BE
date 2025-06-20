package com.swpteam.smokingcessation.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomUtil {

    public String generateRandomUsername() {
        String prefix = "User";
        int randomNumber = new Random().nextInt(900000) + 100000;
        return prefix + randomNumber;
    }
}
