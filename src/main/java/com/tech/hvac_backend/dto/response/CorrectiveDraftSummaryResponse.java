package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class CorrectiveDraftSummaryResponse {

    private String id;
    private String vesselName;
    private String machineTag;
    private String machineModel;
    private String machineLocation;
    private String createdAt;
    private String problemSummary;
    private String machineReturnedToService;

    public CorrectiveDraftSummaryResponse(
            String id,
            String vesselName,
            String machineTag,
            String machineModel,
            String machineLocation,
            String createdAt,
            String problemSummary,
            String machineReturnedToService
    ) {
        this.id = id;
        this.vesselName = vesselName;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineLocation = machineLocation;
        this.createdAt = createdAt;
        this.problemSummary = problemSummary;
        this.machineReturnedToService = machineReturnedToService;
    }

}