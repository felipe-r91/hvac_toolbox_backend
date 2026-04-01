package com.tech.hvac_backend.dto;

import java.time.Instant;

public class SyncResponse {

    private String status;
    private String reportId;
    private String message;
    private Instant timestamp;

    public SyncResponse(String status, String reportId, String message) {
        this.status = status;
        this.reportId = reportId;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public String getStatus() {
        return status;
    }

    public String getReportId() {
        return reportId;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}