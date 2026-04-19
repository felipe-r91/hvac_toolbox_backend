package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class MachinePlanMachineResponse {

    private final String id;
    private final String vesselId;
    private final String location;
    private final String tag;
    private final String model;
    private final String serialNumber;
    private final String type;
    private final String starterType;
    private final String machineTemplateVersionId;
    private final String starterTemplateVersionId;

    public MachinePlanMachineResponse(
            String id,
            String vesselId,
            String location,
            String tag,
            String model,
            String serialNumber,
            String type,
            String starterType,
            String machineTemplateVersionId,
            String starterTemplateVersionId
    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.location = location;
        this.tag = tag;
        this.model = model;
        this.serialNumber = serialNumber;
        this.type = type;
        this.starterType = starterType;
        this.machineTemplateVersionId = machineTemplateVersionId;
        this.starterTemplateVersionId = starterTemplateVersionId;
    }

}