package com.tech.hvac_backend.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiMachineMaintenanceReportResponse {

    private String reportNo;
    private String title;
    private String subtitle;
    private String company;
    private String branch;
    private String date;
    private String serviceOrder;
    private String engineer;
    private String projectManager;
    private String location;

    private String machineStatus;
    private String maintenanceResult;
    private String alarmStatus;

    private String executiveSummary;
    private String maintenanceSummary;

    private List<MachineMaintenanceAlarmItem> alarms;
    private List<MachineMaintenanceActivityItem> activities;
    private List<String> recommendations;

    private String furtherActionRequired;
    private String ehsStatement;

    @Getter
    @Setter
    public static class MachineMaintenanceAlarmItem {
        private String description;
        private String status;
    }

    @Getter
    @Setter
    public static class MachineMaintenanceActivityItem {
        private String category;
        private String task;
        private String tool;
        private String status;
        private String notes;
        private String measuredValue;
        private String unit;
        private String completedAt;
        private List<String> photos;
    }
}
