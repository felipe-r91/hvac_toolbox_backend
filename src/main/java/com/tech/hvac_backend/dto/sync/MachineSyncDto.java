package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MachineSyncDto {

    private String id;
    private String location;
    private String tag;
    private String model;
    private String serialNumber;
    private String type;
    private String starterType;

    public MachineSyncDto() {
    }

}