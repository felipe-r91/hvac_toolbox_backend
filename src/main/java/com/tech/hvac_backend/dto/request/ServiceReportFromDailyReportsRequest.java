package com.tech.hvac_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ServiceReportFromDailyReportsRequest {

    @NotNull
    @Size(min = 2, message = "must contain at least two report ids")
    private List<String> dailyReportIds;
}
