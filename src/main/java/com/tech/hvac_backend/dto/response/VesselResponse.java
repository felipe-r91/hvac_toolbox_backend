package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class VesselResponse {

    private final String id;
    private final String name;
    private final String imoNumber;
    private final String vesselType;
    private final String ownerCustomer;
    private final String vesselContact;
    private final String description;
    private final List<MachineResponse> machines;

    public VesselResponse(
            String id,
            String name,
            String imoNumber,
            String vesselType,
            String ownerCustomer,
            String vesselContact,
            String description,
            List<MachineResponse> machines
    ) {
        this.id = id;
        this.name = name;
        this.imoNumber = imoNumber;
        this.vesselType = vesselType;
        this.ownerCustomer = ownerCustomer;
        this.vesselContact = vesselContact;
        this.description = description;
        this.machines = machines;
    }

}
