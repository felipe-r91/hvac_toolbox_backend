package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiServiceReportFromDailyReportsResult;

public interface ServiceReportFromDailyReportsAiGenerator {

    AiServiceReportFromDailyReportsResult generateServiceReportFromDailyReports(String prompt);
}
