package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PreventiveReportTaskDetailResponse {

    private final String id;
    private final String taskTemplateId;
    private final String category;
    private final String taskName;
    private final String tool;
    private final Boolean checked;
    private final String status;
    private final String notes;
    private final String measuredValue;
    private final String unit;
    private final List<String> photoIds;
    private final List<PhotoDetailResponse> photos;
    private final String completedAt;

    public PreventiveReportTaskDetailResponse(
            String id,
            String taskTemplateId,
            String category,
            String taskName,
            String tool,
            Boolean checked,
            String status,
            String notes,
            String measuredValue,
            String unit,
            List<String> photoIds,
            List<PhotoDetailResponse> photos,
            String completedAt
    ) {
        this.id = id;
        this.taskTemplateId = taskTemplateId;
        this.category = category;
        this.taskName = taskName;
        this.tool = tool;
        this.checked = checked;
        this.status = status;
        this.notes = notes;
        this.measuredValue = measuredValue;
        this.unit = unit;
        this.photoIds = photoIds;
        this.photos = photos;
        this.completedAt = completedAt;
    }

}
