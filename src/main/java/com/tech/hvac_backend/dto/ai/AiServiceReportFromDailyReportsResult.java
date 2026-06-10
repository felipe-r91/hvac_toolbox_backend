package com.tech.hvac_backend.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiServiceReportFromDailyReportsResult {

    private AiServiceReportResponse serviceReport;
    private List<String> selectedPhotoIds;
}
