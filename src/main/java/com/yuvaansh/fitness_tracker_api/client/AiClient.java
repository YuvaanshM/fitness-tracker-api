package com.yuvaansh.fitness_tracker_api.client;

/**
 * Provider-neutral text generation boundary used by the application's AI
 * features. Provider-specific payloads remain behind this interface.
 */
public interface AiClient {

    String generate(String systemPrompt, String userPrompt);
}
