package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class MachineTimelineItemResponse {

    private final String id;
    private final String type;
    private final String date;
    private final String status;
    private final String title;
    private final String summary;

    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;

    private final String linkedCorrectiveDraftId;
    private final String sourcePreventiveReportId;

    public MachineTimelineItemResponse(
            String id,
            String type,
            String date,
            String status,
            String title,
            String summary,
            String failureComponent,
            String failureMode,
            String failureCode,
            String linkedCorrectiveDraftId,
            String sourcePreventiveReportId
    ) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.status = status;
        this.title = title;
        this.summary = summary;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.linkedCorrectiveDraftId = linkedCorrectiveDraftId;
        this.sourcePreventiveReportId = sourcePreventiveReportId;
    }

}