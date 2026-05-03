package com.tech.hvac_backend.dto.ai;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AiCustomerReportResponse {
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
    private String severity;
    private String finalCondition;

    private String executiveSummary;
    private String conditionFound;
    private List<String> alarms;
    private String operationalImpact;
    private String probableRootCause;
    private List<String> recommendations;
    private String furtherActionRequired;
    private String ehsStatement;
}