package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class CorrectiveDraftDetailResponse {

    private final String id;
    private final String vesselId;
    private final String vesselName;
    private final String machineId;
    private final String machineTag;
    private final String machineModel;
    private final String machineType;
    private final String machineStarterType;
    private final String machineLocation;
    private final String createdAt;

    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;

    private final String problemSummary;
    private final String conditionFound;
    private final String symptomsObserved;
    private final String alarmsObserved;
    private final String operationalImpact;

    private final String preliminaryDiagnosis;
    private final String confirmedCause;

    private final String correctiveAction;
    private final String recommendations;
    private final String furtherActionRequired;

    private final String machineReturnedToService;
    private final Boolean synced;
    private final List<CorrectivePhotoDetailResponse> photos;

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
            String failureComponent,
            String failureMode,
            String failureCode,
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
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
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