package com.tech.hvac_backend.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
import com.tech.hvac_backend.dto.ai.AiCustomerReportResponse;
import com.tech.hvac_backend.dto.ai.AiDailyReportResponse;
import com.tech.hvac_backend.dto.ai.AiHealthCheckReportResponse;
import com.tech.hvac_backend.dto.ai.AiMachineMaintenanceReportResponse;
import com.tech.hvac_backend.dto.ai.AiServiceReportResponse;
import com.tech.hvac_backend.dto.ai.AiServiceReportFromDailyReportsResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class OpenAiReportGenerationService implements ServiceReportFromDailyReportsAiGenerator {

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
        return generateReport(prompt, AiCustomerReportResponse.class);
    }

    public AiServiceReportResponse generateServiceReport(String prompt) {
        return generateReport(prompt, AiServiceReportResponse.class);
    }

    @Override
    public AiServiceReportFromDailyReportsResult generateServiceReportFromDailyReports(String prompt) {
        return generateReport(prompt, AiServiceReportFromDailyReportsResult.class);
    }

    public AiDailyReportResponse generateDailyReport(String prompt) {
        return generateReport(prompt, AiDailyReportResponse.class);
    }

    public AiMachineMaintenanceReportResponse generateMachineMaintenanceReport(String prompt) {
        return generateReport(prompt, AiMachineMaintenanceReportResponse.class);
    }

    public AiHealthCheckReportResponse generateHealthCheckReport(String prompt) {
        return generateReport(prompt, AiHealthCheckReportResponse.class);
    }

    private <T> T generateReport(String prompt, Class<T> responseType) {
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
            return objectMapper.readValue(cleanJson(text), responseType);
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
