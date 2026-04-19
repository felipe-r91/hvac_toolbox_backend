package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class MaintenanceTemplateLibraryTaskResponse {

    private final String id;
    private final String category;
    private final String task;
    private final String tool;
    private final String unit;
    private final Boolean required;
    private final Boolean measurable;
    private final Boolean photoRequiredOnFault;
    private final Boolean photoRequiredOnAttention;

    public MaintenanceTemplateLibraryTaskResponse(
            String id,
            String category,
            String task,
            String tool,
            String unit,
            Boolean required,
            Boolean measurable,
            Boolean photoRequiredOnFault,
            Boolean photoRequiredOnAttention
    ) {
        this.id = id;
        this.category = category;
        this.task = task;
        this.tool = tool;
        this.unit = unit;
        this.required = required;
        this.measurable = measurable;
        this.photoRequiredOnFault = photoRequiredOnFault;
        this.photoRequiredOnAttention = photoRequiredOnAttention;
    }

}