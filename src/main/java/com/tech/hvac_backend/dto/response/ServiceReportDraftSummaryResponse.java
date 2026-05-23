package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class ServiceReportDraftSummaryResponse {

    private final String id;
    private final String vesselName;
    private final String vesselImo;
    private final String machineTag;
    private final String machineModel;
    private final String machineLocation;
    private final String createdAt;
    private final String workPerformed;
    private final String machineReturnedToService;
    private final String reportCategory;

    public ServiceReportDraftSummaryResponse(
            String id,
            String vesselName,
            String vesselImo,
            String machineTag,
            String machineModel,
            String machineLocation,
            String createdAt,
            String workPerformed,
            String machineReturnedToService,
            String reportCategory
    ) {
        this.id = id;
        this.vesselName = vesselName;
        this.vesselImo = vesselImo;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineLocation = machineLocation;
        this.createdAt = createdAt;
        this.workPerformed = workPerformed;
        this.machineReturnedToService = machineReturnedToService;
        this.reportCategory = reportCategory;
    }

}
