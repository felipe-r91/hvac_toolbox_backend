package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.customerReport.CreateCustomerReportRequest;
import com.tech.hvac_backend.dto.customerReport.CustomerReportResponse;
import com.tech.hvac_backend.service.CustomerReportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer-reports")
public class CustomerReportController {

    private final CustomerReportService customerReportService;

    public CustomerReportController(CustomerReportService customerReportService) {
        this.customerReportService = customerReportService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomerReportResponse createCustomerReport(
            @RequestPart("metadata") CreateCustomerReportRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            return customerReportService.createCustomerReport(request, file);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping
    public List<CustomerReportResponse> findReports(
            @RequestParam(required = false) String vesselId,
            @RequestParam(required = false) String machineId,
            @RequestParam(required = false) String reportType
    ) {
        return customerReportService.findReports(vesselId, machineId, reportType);
    }

    @GetMapping("/{reportId}")
    public CustomerReportResponse findById(@PathVariable UUID reportId) {
        return customerReportService.findById(reportId);
    }

    @GetMapping("/{reportId}/download-url")
    public String generateDownloadUrl(@PathVariable UUID reportId) {
        return customerReportService.generateDownloadUrl(reportId);
    }
}