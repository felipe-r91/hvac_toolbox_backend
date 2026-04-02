package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class VesselSyncDto {

    private String id;
    private String name;
    private String imoNumber;
    private String description;
    private List<MachineSyncDto> machines;

    public VesselSyncDto() {
    }

}