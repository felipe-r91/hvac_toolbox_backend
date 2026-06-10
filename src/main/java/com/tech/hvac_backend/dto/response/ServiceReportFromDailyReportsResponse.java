package com.tech.hvac_backend.dto.response;

import com.tech.hvac_backend.dto.ai.AiServiceReportResponse;
import lombok.Getter;

@Getter
public class ServiceReportFromDailyReportsResponse {

    private final ServiceReportDraftDetailResponse sourceReport;
    private final AiServiceReportResponse aiReport;

    public ServiceReportFromDailyReportsResponse(
            ServiceReportDraftDetailResponse sourceReport,
            AiServiceReportResponse aiReport
    ) {
        this.sourceReport = sourceReport;
        this.aiReport = aiReport;
    }
}
