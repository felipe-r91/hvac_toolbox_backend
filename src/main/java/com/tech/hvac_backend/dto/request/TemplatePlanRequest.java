package com.tech.hvac_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TemplatePlanRequest {

    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 20)
    private String templateType;

    @Size(max = 500)
    private String notes;

    @Valid
    @NotEmpty
    private List<TemplatePlanTaskRequest> tasks;
}
