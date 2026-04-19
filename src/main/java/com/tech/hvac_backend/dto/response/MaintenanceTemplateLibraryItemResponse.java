package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MaintenanceTemplateLibraryItemResponse {

    private final String code;
    private final String name;
    private final String templateType;
    private final String versionId;
    private final Integer versionNumber;
    private final List<MaintenanceTemplateLibraryTaskResponse> tasks;

    public MaintenanceTemplateLibraryItemResponse(
            String code,
            String name,
            String templateType,
            String versionId,
            Integer versionNumber,
            List<MaintenanceTemplateLibraryTaskResponse> tasks
    ) {
        this.code = code;
        this.name = name;
        this.templateType = templateType;
        this.versionId = versionId;
        this.versionNumber = versionNumber;
        this.tasks = tasks;
    }

}