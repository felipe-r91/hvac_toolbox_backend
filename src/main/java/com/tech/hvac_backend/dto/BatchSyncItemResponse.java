package com.tech.hvac_backend.dto;

public class BatchSyncItemResponse {

    private String reportId;
    private String status;
    private String message;

    public BatchSyncItemResponse(String reportId, String status, String message) {
        this.reportId = reportId;
        this.status = status;
        this.message = message;
    }

    public String getReportId() {
        return reportId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}