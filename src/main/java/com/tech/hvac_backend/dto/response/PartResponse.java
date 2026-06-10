package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PartResponse {

    private final String id;
    private final String jciPartNumber;
    private final String manufacturerModel;
    private final String manufacturerCode;
    private final String tag;
    private final List<String> machinesModelHavingIt;
    private final String description;
    private final String partPhotoId;
    private final String partPhotoPreviewUrl;

    public PartResponse(
            String id,
            String jciPartNumber,
            String manufacturerModel,
            String manufacturerCode,
            String tag,
            List<String> machinesModelHavingIt,
            String description,
            String partPhotoId,
            String partPhotoPreviewUrl
    ) {
        this.id = id;
        this.jciPartNumber = jciPartNumber;
        this.manufacturerModel = manufacturerModel;
        this.manufacturerCode = manufacturerCode;
        this.tag = tag;
        this.machinesModelHavingIt = machinesModelHavingIt;
        this.description = description;
        this.partPhotoId = partPhotoId;
        this.partPhotoPreviewUrl = partPhotoPreviewUrl;
    }
}
