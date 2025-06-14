package com.swpteam.smokingcessation.utils;

import java.text.Normalizer;
import java.util.Random;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-)|(-$)");
    private static final int RANDOM_SUFFIX_LENGTH = 8;
    private static final String RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }

        input = input.replaceAll("đ", "d").replaceAll("Đ", "D");

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase();
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        return slug;
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_CHARS.charAt(RANDOM.nextInt(RANDOM_CHARS.length())));
        }
        return sb.toString();
    }

    public static String appendRandomSuffix(String slug) {
        return slug + "-" + generateRandomString(RANDOM_SUFFIX_LENGTH);
    }
}
