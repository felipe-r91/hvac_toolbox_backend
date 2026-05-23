package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class DailyDraftDetailResponse {

    private final String id;
    private final String vesselId;
    private final String vesselName;
    private final String vesselImo;
    private final String vesselType;
    private final String ownerCustomer;
    private final String vesselContact;
    private final String machineId;
    private final String machineTag;
    private final String machineModel;
    private final String machineSerialNumber;
    private final String machineType;
    private final String machineStarterType;
    private final String machineLocation;
    private final String machineRefrigerant;
    private final String machineOilType;
    private final String machineControlSystem;
    private final String machineSoftwareVersion;
    private final String machineCompressorType;
    private final String machineMfg;
    private final String machinePhotoId;
    private final String machinePhotoPreviewUrl;
    private final String createdAt;

    private final Boolean alarmPresent;
    private final String reportCategory;

    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;
    private final String failureNotes;

    private final String workConductedToday;
    private final String furtherActions;

    private final Boolean synced;
    private final List<PhotoDetailResponse> photos;

    public DailyDraftDetailResponse(
            String id,
            String vesselId,
            String vesselName,
            String vesselImo,
            String vesselType,
            String ownerCustomer,
            String vesselContact,
            String machineId,
            String machineTag,
            String machineModel,
            String machineSerialNumber,
            String machineType,
            String machineStarterType,
            String machineLocation,
            String machineRefrigerant,
            String machineOilType,
            String machineControlSystem,
            String machineSoftwareVersion,
            String machineCompressorType,
            String machineMfg,
            String machinePhotoId,
            String machinePhotoPreviewUrl,
            String createdAt,
            Boolean alarmPresent,
            String reportCategory,
            String failureComponent,
            String failureMode,
            String failureCode,
            String failureNotes,
            String workConductedToday,
            String furtherActions,
            Boolean synced,
            List<PhotoDetailResponse> photos
    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.vesselName = vesselName;
        this.vesselImo = vesselImo;
        this.vesselType = vesselType;
        this.ownerCustomer = ownerCustomer;
        this.vesselContact = vesselContact;
        this.machineId = machineId;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineSerialNumber = machineSerialNumber;
        this.machineType = machineType;
        this.machineStarterType = machineStarterType;
        this.machineLocation = machineLocation;
        this.machineRefrigerant = machineRefrigerant;
        this.machineOilType = machineOilType;
        this.machineControlSystem = machineControlSystem;
        this.machineSoftwareVersion = machineSoftwareVersion;
        this.machineCompressorType = machineCompressorType;
        this.machineMfg = machineMfg;
        this.machinePhotoId = machinePhotoId;
        this.machinePhotoPreviewUrl = machinePhotoPreviewUrl;
        this.createdAt = createdAt;
        this.alarmPresent = alarmPresent;
        this.reportCategory = reportCategory;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.failureNotes = failureNotes;
        this.workConductedToday = workConductedToday;
        this.furtherActions = furtherActions;
        this.synced = synced;
        this.photos = photos;
    }
}
