package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MaintenanceTemplateLibraryResponse {

    private final List<MaintenanceTemplateLibraryItemResponse> templates;

    public MaintenanceTemplateLibraryResponse(List<MaintenanceTemplateLibraryItemResponse> templates) {
        this.templates = templates;
    }

}