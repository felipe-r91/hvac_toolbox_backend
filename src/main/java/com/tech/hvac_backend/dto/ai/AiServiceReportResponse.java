package com.tech.hvac_backend.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiServiceReportResponse {

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
    private String serviceResult;
    private String machineReturnedToService;

    private String executiveSummary;
    private String conditionFound;

    private List<ServiceAlarmItem> alarms;
    private List<String> workConducted;
    private List<String> recommendations;

    private String furtherActionRequired;
    private String ehsStatement;

    @Getter
    @Setter
    public static class ServiceAlarmItem {
        private String description;
        private String status;
    }
}