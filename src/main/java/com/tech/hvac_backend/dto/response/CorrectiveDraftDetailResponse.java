package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class CorrectiveDraftDetailResponse {

    private String id;
    private String vesselId;
    private String vesselName;
    private String machineId;
    private String machineTag;
    private String machineModel;
    private String machineType;
    private String machineStarterType;
    private String machineLocation;
    private String createdAt;

    private String problemSummary;
    private String conditionFound;
    private String symptomsObserved;
    private String alarmsObserved;
    private String operationalImpact;

    private String preliminaryDiagnosis;
    private String confirmedCause;

    private String correctiveAction;
    private String recommendations;
    private String furtherActionRequired;

    private String machineReturnedToService;
    private Boolean synced;

    private List<CorrectivePhotoDetailResponse> photos;

    public CorrectiveDraftDetailResponse(
            String id,
            String vesselId,
            String vesselName,
            String machineId,
            String machineTag,
            String machineModel,
            String machineType,
            String machineStarterType,
            String machineLocation,
            String createdAt,
            String problemSummary,
            String conditionFound,
            String symptomsObserved,
            String alarmsObserved,
            String operationalImpact,
            String preliminaryDiagnosis,
            String confirmedCause,
            String correctiveAction,
            String recommendations,
            String furtherActionRequired,
            String machineReturnedToService,
            Boolean synced,
            List<CorrectivePhotoDetailResponse> photos
    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.vesselName = vesselName;
        this.machineId = machineId;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineType = machineType;
        this.machineStarterType = machineStarterType;
        this.machineLocation = machineLocation;
        this.createdAt = createdAt;
        this.problemSummary = problemSummary;
        this.conditionFound = conditionFound;
        this.symptomsObserved = symptomsObserved;
        this.alarmsObserved = alarmsObserved;
        this.operationalImpact = operationalImpact;
        this.preliminaryDiagnosis = preliminaryDiagnosis;
        this.confirmedCause = confirmedCause;
        this.correctiveAction = correctiveAction;
        this.recommendations = recommendations;
        this.furtherActionRequired = furtherActionRequired;
        this.machineReturnedToService = machineReturnedToService;
        this.synced = synced;
        this.photos = photos;
    }

}