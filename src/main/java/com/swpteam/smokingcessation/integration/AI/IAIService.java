package com.swpteam.smokingcessation.integration.AI;

public interface IAIService {

    public String chat(String prompt);

    public String chatWithPlatformContext(String message);
}
