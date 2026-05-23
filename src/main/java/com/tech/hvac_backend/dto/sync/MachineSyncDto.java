package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MachineSyncDto {

    private String id;
    private String vesselId;
    private String location;
    private String tag;
    private String model;
    private String serialNumber;
    private String type;
    private String starterType;
    private String refrigerant;
    private String oilType;
    private String controlSystem;
    private String softwareVersion;
    private String compressorType;
    private String mfg;

    private String machinePhotoId;
    private String machinePhotoPreviewUrl;

    public MachineSyncDto() {
    }
}
