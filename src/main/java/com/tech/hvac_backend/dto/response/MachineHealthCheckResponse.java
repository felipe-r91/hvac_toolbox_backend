package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MachineHealthCheckResponse {

    private final MachinePlanMachineResponse machine;
    private final String templateCode;
    private final String templateVersionId;
    private final Integer templateVersionNumber;
    private final List<MaintenancePlanTaskResponse> tasks;

    public MachineHealthCheckResponse(
            MachinePlanMachineResponse machine,
            String templateCode,
            String templateVersionId,
            Integer templateVersionNumber,
            List<MaintenancePlanTaskResponse> tasks
    ) {
        this.machine = machine;
        this.templateCode = templateCode;
        this.templateVersionId = templateVersionId;
        this.templateVersionNumber = templateVersionNumber;
        this.tasks = tasks;
    }

}
