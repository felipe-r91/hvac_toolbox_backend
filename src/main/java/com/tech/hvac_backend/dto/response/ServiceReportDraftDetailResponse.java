package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ServiceReportDraftDetailResponse {

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

    private final String workPerformed;
    private final String recommendations;
    private final String furtherActionRequired;
    private final String sourcePreventiveReportId;
    private final String machineReturnedToService;
    private final Boolean synced;
    private final List<PhotoDetailResponse> photos;
    private final String reportCategory;

    public ServiceReportDraftDetailResponse(
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
            String workPerformed,
            String recommendations,
            String furtherActionRequired,
            String sourcePreventiveReportId,
            String machineReturnedToService,
            Boolean synced,
            List<PhotoDetailResponse> photos,
            String reportCategory
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
        this.workPerformed = workPerformed;
        this.recommendations = recommendations;
        this.furtherActionRequired = furtherActionRequired;
        this.sourcePreventiveReportId = sourcePreventiveReportId;
        this.machineReturnedToService = machineReturnedToService;
        this.synced = synced;
        this.photos = photos;
        this.reportCategory = reportCategory;
    }

}
