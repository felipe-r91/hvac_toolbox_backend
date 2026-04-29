package com.tech.hvac_backend.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class SyncResponse {

    private final String status;
    private final String reportId;
    private final String message;
    private final Instant timestamp;

    public SyncResponse(String status, String reportId, String message) {
        this.status = status;
        this.reportId = reportId;
        this.message = message;
        this.timestamp = Instant.now();
    }

}