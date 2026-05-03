package com.tech.hvac_backend.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
import com.tech.hvac_backend.dto.ai.AiCustomerReportResponse;
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

    public AiCustomerReportResponse generateCustomerReport(String prompt) {
        String finalPrompt = """
                You must return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the response in ```json.
                Do not add explanations before or after the JSON.

                The JSON must match this structure exactly:

                {
                  "reportNo": "",
                  "title": "",
                  "subtitle": "",
                  "company": "Johnson Controls",
                  "branch": "",
                  "date": "",
                  "serviceOrder": "",
                  "engineer": "",
                  "projectManager": "",
                  "location": "",
                  "machineStatus": "",
                  "severity": "",
                  "finalCondition": "",
                  "executiveSummary": "",
                  "conditionFound": "",
                  "alarms": [],
                  "operationalImpact": "",
                  "probableRootCause": "",
                  "recommendations": [],
                  "furtherActionRequired": "",
                  "ehsStatement": ""
                }

                User report data:
                %s
                """.formatted(prompt);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(model)
                .input(finalPrompt)
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
            return objectMapper.readValue(cleanJson(text), AiCustomerReportResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse OpenAI JSON response: " + text, e);
        }
    }

    private String cleanJson(String text) {
        return text
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}