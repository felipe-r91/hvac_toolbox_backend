package com.tech.hvac_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CfrSyncResponse {
    private String id;
    private String status;
}