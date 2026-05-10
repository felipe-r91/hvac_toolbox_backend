package com.tech.hvac_backend.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiDailyReportResponse {

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
    private String alarmStatus;

    private String executiveSummary;
    private String dailySummary;
    private String failureNotes;

    private List<DailyAlarmItem> alarms;
    private List<String> workConductedToday;
    private List<String> recommendations;

    private String furtherActions;
    private String ehsStatement;

    @Getter
    @Setter
    public static class DailyAlarmItem {
        private String description;
        private String status;
    }
}
