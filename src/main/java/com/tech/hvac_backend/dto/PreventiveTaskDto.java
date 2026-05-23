package com.tech.hvac_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PreventiveTaskDto {

    private String id;
    private String category;
    private String task;
    private String tool;
    private Boolean checked;
    private String status;
    private String notes;
    private String measuredValue;
    private String unit;
    private List<String> photoIds;
    private String completedAt;

    public PreventiveTaskDto() {
    }

}
