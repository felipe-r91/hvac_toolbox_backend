package com.tech.hvac_backend.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ApiErrorResponse {

    private final String status;
    private final int code;
    private final String message;
    private final Instant timestamp;
    private final String path;

    public ApiErrorResponse(String status, int code, String message, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
        this.path = path;
    }

}