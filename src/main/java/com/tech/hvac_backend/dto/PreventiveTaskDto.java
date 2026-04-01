package com.tech.hvac_backend.dto;

import lombok.Getter;

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
    private String completedAt;

    public PreventiveTaskDto() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setMeasuredValue(String measuredValue) {
        this.measuredValue = measuredValue;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}