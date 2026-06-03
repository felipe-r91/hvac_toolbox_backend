package com.tech.hvac_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplatePlanTaskRequest {

    @Size(max = 80)
    private String id;

    @Size(max = 80)
    private String taskCode;

    @NotBlank
    @Size(max = 60)
    private String category;

    @NotBlank
    @Size(max = 500)
    private String task;

    @Size(max = 255)
    private String tool;

    @Size(max = 30)
    private String unit;

    private Boolean required;
    private Boolean measurable;
    private Boolean photoRequiredOnFault;
    private Boolean photoRequiredOnAttention;
}
