package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MachinePlanResponse {

    private final MachinePlanMachineResponse machine;
    private final List<MaintenancePlanTaskResponse> tasks;

    public MachinePlanResponse(
            MachinePlanMachineResponse machine,
            List<MaintenancePlanTaskResponse> tasks
    ) {
        this.machine = machine;
        this.tasks = tasks;
    }

}