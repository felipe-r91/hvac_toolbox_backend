package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.ai.AiCustomerReportResponse;
import com.tech.hvac_backend.dto.ai.AiDailyReportResponse;
import com.tech.hvac_backend.dto.ai.AiServiceReportResponse;
import com.tech.hvac_backend.service.CfrAiReportService;
import com.tech.hvac_backend.service.ServiceReportAiReportService;
import com.tech.hvac_backend.service.DailyAiReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-reports")
public class AiReportController {

    private final CfrAiReportService cfrAiReportService;
    private final ServiceReportAiReportService serviceReportAiReportService;
    private final DailyAiReportService dailyAiReportService;

    public AiReportController(
            CfrAiReportService cfrAiReportService,
            ServiceReportAiReportService serviceReportAiReportService,
            DailyAiReportService dailyAiReportService
    ) {
        this.cfrAiReportService = cfrAiReportService;
        this.serviceReportAiReportService = serviceReportAiReportService;
        this.dailyAiReportService = dailyAiReportService;
    }

    @PostMapping("/cfr/{draftId}/generate")
    public ResponseEntity<AiCustomerReportResponse> generateCfrReport(
            @PathVariable String draftId
    ) {
        return ResponseEntity.ok(cfrAiReportService.generate(draftId));
    }

    @PostMapping("/service-report/{draftId}/generate")
    public ResponseEntity<AiServiceReportResponse> generateServiceReport(
            @PathVariable String draftId
    ) {
        return ResponseEntity.ok(serviceReportAiReportService.generate(draftId));
    }

    @PostMapping("/daily/{draftId}/generate")
    public ResponseEntity<AiDailyReportResponse> generateDailyReport(
            @PathVariable String draftId
    ) {
        return ResponseEntity.ok(dailyAiReportService.generate(draftId));
    }
}
