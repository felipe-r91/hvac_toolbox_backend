package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class MachineResponse {

    private final String id;
    private final String vesselId;
    private final String location;
    private final String tag;
    private final String model;
    private final String serialNumber;
    private final String type;
    private final String starterType;

    public MachineResponse(
            String id,
            String vesselId,
            String location,
            String tag,
            String model,
            String serialNumber,
            String type,
            String starterType
    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.location = location;
        this.tag = tag;
        this.model = model;
        this.serialNumber = serialNumber;
        this.type = type;
        this.starterType = starterType;
    }

}