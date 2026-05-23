package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DailySyncRequest {

    private String id;
    private String vesselId;
    private String vesselName;
    private String vesselImo;
    private String vesselType;
    private String ownerCustomer;
    private String vesselContact;
    private String machineId;
    private String machineTag;
    private String machineModel;
    private String machineSerialNumber;
    private String machineType;
    private String machineStarterType;
    private String machineLocation;
    private String machineRefrigerant;
    private String machineOilType;
    private String machineControlSystem;
    private String machineSoftwareVersion;
    private String machineCompressorType;
    private String machineMfg;
    private String createdAt;

    private Boolean alarmPresent;
    private String reportCategory;

    private String failureComponent;
    private String failureMode;
    private String failureCode;
    private String failureNotes;

    private String workConductedToday;
    private String furtherActions;

    private List<CorrectivePhotoDto> photos;

    private Boolean synced;
}
