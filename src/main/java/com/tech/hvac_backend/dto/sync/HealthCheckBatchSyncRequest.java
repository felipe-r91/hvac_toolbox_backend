package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HealthCheckBatchSyncRequest {

    private List<HealthCheckSyncRequest> reports;

    public HealthCheckBatchSyncRequest() {
    }
}
