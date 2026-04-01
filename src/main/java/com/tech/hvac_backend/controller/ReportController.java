package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.CorrectiveDraftDetailResponse;
import com.tech.hvac_backend.dto.response.CorrectiveDraftSummaryResponse;
import com.tech.hvac_backend.dto.response.PreventiveReportDetailResponse;
import com.tech.hvac_backend.dto.response.PreventiveReportSummaryResponse;
import com.tech.hvac_backend.service.ReportQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportQueryService reportQueryService;

    public ReportController(ReportQueryService reportQueryService) {
        this.reportQueryService = reportQueryService;
    }

    @GetMapping("/preventive")
    public ResponseEntity<List<PreventiveReportSummaryResponse>> getPreventiveReports() {
        return ResponseEntity.ok(reportQueryService.getAllPreventiveReports());
    }

    @GetMapping("/corrective")
    public ResponseEntity<List<CorrectiveDraftSummaryResponse>> getCorrectiveDrafts() {
        return ResponseEntity.ok(reportQueryService.getAllCorrectiveDrafts());
    }

    @GetMapping("/preventive/{id}")
    public ResponseEntity<PreventiveReportDetailResponse> getPreventiveReportById(@PathVariable String id) {
        return ResponseEntity.ok(reportQueryService.getPreventiveReportById(id));
    }

    @GetMapping("/corrective/{id}")
    public ResponseEntity<CorrectiveDraftDetailResponse> getCorrectiveDraftById(@PathVariable String id) {
        return ResponseEntity.ok(reportQueryService.getCorrectiveDraftById(id));
    }
}