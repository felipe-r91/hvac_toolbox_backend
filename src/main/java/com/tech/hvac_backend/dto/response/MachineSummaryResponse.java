package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class MachineSummaryResponse {

    private final String machineId;
    private final String vesselId;
    private final String vesselName;
    private final String machineTag;
    private final String model;
    private final String serialNumber;
    private final String type;
    private final String starterType;
    private final String location;

    private final String latestReportDate;
    private final String latestReportType;
    private final String latestKnownStatus;

    private final long preventiveReportCount;
    private final long correctiveDraftCount;
    private final long cfrDraftCount;

    public MachineSummaryResponse(
            String machineId,
            String vesselId,
            String vesselName,
            String machineTag,
            String model,
            String serialNumber,
            String type,
            String starterType,
            String location,
            String latestReportDate,
            String latestReportType,
            String latestKnownStatus,
            long preventiveReportCount,
            long correctiveDraftCount,
            long cfrDraftCount
    ) {
        this.machineId = machineId;
        this.vesselId = vesselId;
        this.vesselName = vesselName;
        this.machineTag = machineTag;
        this.model = model;
        this.serialNumber = serialNumber;
        this.type = type;
        this.starterType = starterType;
        this.location = location;
        this.latestReportDate = latestReportDate;
        this.latestReportType = latestReportType;
        this.latestKnownStatus = latestKnownStatus;
        this.preventiveReportCount = preventiveReportCount;
        this.correctiveDraftCount = correctiveDraftCount;
        this.cfrDraftCount = cfrDraftCount;
    }
}