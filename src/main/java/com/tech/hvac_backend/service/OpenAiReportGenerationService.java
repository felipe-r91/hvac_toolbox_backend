package com.tech.hvac_backend.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
import com.tech.hvac_backend.dto.ai.CorrectiveAiDraftResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class OpenAiReportGenerationService {

    private final OpenAIClient client;
    private final String model;
    private final ObjectMapper objectMapper;

    public OpenAiReportGenerationService(
            @Value("${openai.model}") String model,
            ObjectMapper objectMapper
    ) {
        this.client = OpenAIOkHttpClient.fromEnv();
        this.model = model;
        this.objectMapper = objectMapper;
    }

    public CorrectiveAiDraftResponse generateCorrectiveDraft(String prompt) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(model)
                .input(prompt)
                .build();

        var response = client.responses().create(params);

        String text = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(ResponseOutputText::text)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("OpenAI returned no text output"));

        try {
            return objectMapper.readValue(text, CorrectiveAiDraftResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse OpenAI JSON response: " + text, e);
        }
    }
}