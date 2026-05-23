package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.*;
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

    @GetMapping("/service-report")
    public ResponseEntity<List<ServiceReportDraftSummaryResponse>> getServiceReportDrafts() {
        return ResponseEntity.ok(reportQueryService.getAllServiceReportDrafts());
    }

    @GetMapping("/cfr")
    public ResponseEntity<List<CfrDraftSummaryResponse>> getCfrDrafts() {
        return ResponseEntity.ok(reportQueryService.getAllCfrDrafts());
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyDraftSummaryResponse>> getDailyDrafts() {
        return ResponseEntity.ok(reportQueryService.getAllDailyDrafts());
    }

    @GetMapping("/preventive/{id}")
    public ResponseEntity<PreventiveReportDetailResponse> getPreventiveReportById(@PathVariable String id) {
        return ResponseEntity.ok(reportQueryService.getPreventiveReportById(id));
    }

    @GetMapping("/service-report/{id}")
    public ResponseEntity<ServiceReportDraftDetailResponse> getServiceReportDraftById(@PathVariable String id) {
        return ResponseEntity.ok(reportQueryService.getServiceReportDraftById(id));
    }

    @GetMapping("/cfr/{id}")
    public ResponseEntity<CfrDraftDetailResponse> getCfrDraftById(@PathVariable String id) {
        return ResponseEntity.ok(reportQueryService.getCfrDraftById(id));
    }

    @GetMapping("/daily/{id}")
    public ResponseEntity<DailyDraftDetailResponse> getDailyDraftById(@PathVariable String id) {
        return ResponseEntity.ok(reportQueryService.getDailyDraftById(id));
    }
}
