package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.ai.AiCustomerReportResponse;
import com.tech.hvac_backend.dto.ai.AiDailyReportResponse;
import com.tech.hvac_backend.dto.ai.AiHealthCheckReportResponse;
import com.tech.hvac_backend.dto.ai.AiMachineMaintenanceReportResponse;
import com.tech.hvac_backend.dto.ai.AiServiceReportResponse;
import com.tech.hvac_backend.dto.request.ServiceReportFromDailyReportsRequest;
import com.tech.hvac_backend.dto.response.ServiceReportFromDailyReportsResponse;
import com.tech.hvac_backend.service.CfrAiReportService;
import com.tech.hvac_backend.service.HealthCheckAiReportService;
import com.tech.hvac_backend.service.MachineMaintenanceAiReportService;
import com.tech.hvac_backend.service.ServiceReportAiReportService;
import com.tech.hvac_backend.service.ServiceReportFromDailyReportsService;
import com.tech.hvac_backend.service.DailyAiReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-reports")
public class AiReportController {

    private final CfrAiReportService cfrAiReportService;
    private final ServiceReportAiReportService serviceReportAiReportService;
    private final DailyAiReportService dailyAiReportService;
    private final MachineMaintenanceAiReportService machineMaintenanceAiReportService;
    private final HealthCheckAiReportService healthCheckAiReportService;
    private final ServiceReportFromDailyReportsService serviceReportFromDailyReportsService;

    public AiReportController(
            CfrAiReportService cfrAiReportService,
            ServiceReportAiReportService serviceReportAiReportService,
            DailyAiReportService dailyAiReportService,
            MachineMaintenanceAiReportService machineMaintenanceAiReportService,
            HealthCheckAiReportService healthCheckAiReportService,
            ServiceReportFromDailyReportsService serviceReportFromDailyReportsService
    ) {
        this.cfrAiReportService = cfrAiReportService;
        this.serviceReportAiReportService = serviceReportAiReportService;
        this.dailyAiReportService = dailyAiReportService;
        this.machineMaintenanceAiReportService = machineMaintenanceAiReportService;
        this.healthCheckAiReportService = healthCheckAiReportService;
        this.serviceReportFromDailyReportsService = serviceReportFromDailyReportsService;
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

    @PostMapping("/service-report/from-daily-reports/generate")
    public ResponseEntity<ServiceReportFromDailyReportsResponse> generateServiceReportFromDailyReports(
            @Valid @RequestBody ServiceReportFromDailyReportsRequest request
    ) {
        return ResponseEntity.ok(serviceReportFromDailyReportsService.generate(request));
    }

    @PostMapping("/daily/{draftId}/generate")
    public ResponseEntity<AiDailyReportResponse> generateDailyReport(
            @PathVariable String draftId
    ) {
        return ResponseEntity.ok(dailyAiReportService.generate(draftId));
    }

    @PostMapping("/machine-maintenance/{reportId}/generate")
    public ResponseEntity<AiMachineMaintenanceReportResponse> generateMachineMaintenanceReport(
            @PathVariable String reportId
    ) {
        return ResponseEntity.ok(machineMaintenanceAiReportService.generate(reportId));
    }

    @PostMapping("/health-check/{reportId}/generate")
    public ResponseEntity<AiHealthCheckReportResponse> generateHealthCheckReport(
            @PathVariable String reportId
    ) {
        return ResponseEntity.ok(healthCheckAiReportService.generate(reportId));
    }
}
