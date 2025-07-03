package com.swpteam.smokingcessation.feature.integration.AI;

public interface IAIService {

    String chat(String prompt);

    String chatWithPlatformContext(String message);
}
