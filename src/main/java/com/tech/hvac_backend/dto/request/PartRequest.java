package com.tech.hvac_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PartRequest {

    @NotBlank
    private String jciPartNumber;

    @NotBlank
    private String manufacturerModel;

    @NotBlank
    private String manufacturerCode;

    @NotBlank
    private String tag;

    @NotNull
    private List<@NotBlank String> machinesModelHavingIt;

    @NotBlank
    @Size(max = 4000)
    private String description;

    public PartRequest() {
    }
}
