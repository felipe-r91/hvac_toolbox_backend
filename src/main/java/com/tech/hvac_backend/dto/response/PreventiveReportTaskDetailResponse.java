package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class PreventiveReportTaskDetailResponse {

    private String id;
    private String taskTemplateId;
    private String category;
    private String taskName;
    private String tool;
    private Boolean checked;
    private String status;
    private String notes;
    private String measuredValue;
    private String unit;
    private String completedAt;

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
        this.completedAt = completedAt;
    }

}