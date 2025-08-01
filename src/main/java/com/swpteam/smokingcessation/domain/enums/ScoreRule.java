package com.swpteam.smokingcessation.domain.enums;

import lombok.Getter;

@Getter
public enum ScoreRule {

    PHASE_SUCCESS(300, "Phase completed "),

    PHASE_FAIL(-40, "Phase failed"),

    NO_SMOKE(30, "No smoking today"),

    REDUCE_SMOKE(10, "Smoked less than yesterday"),

    INCREASE_SMOKE(-5, "Smoked more than yesterday"),

    PLAN_SUCCESS(1000, "Plan complete"),

    REPORT_DAY_HAS(5, "Reported"),

    REPORT_DAY_MISS(-2, "Report miss"),

    NO_SMOKING_FOR_10DAYS(100, "10 days nonsmoked"),

    NO_SMOKING_FOR_30DAYS(500, "30 days nonsmoked"),

    NO_SMOKING_FOR_180DAYS(1000, "180 days nonsmoked"),

    NO_SMOKING_FOR_365DAYS(3000, "365 days nonsmoked"),

    REPORTED_ALL_PHASE(100, "no missing record in a phase"),

    REPORT_ALL_PLAN(400,"no missing record in all phases"),

    ACHIEVEMENT_EARNED(3000,"Member reach a goal"),

    NO_SMOKING_DAY_AUTO_COUNTER(10,"daily bonus for auto counter");

    private final int point;
    private final String label;

    ScoreRule(int point, String label) {
        this.point = point;
        this.label = label;
    }

}
