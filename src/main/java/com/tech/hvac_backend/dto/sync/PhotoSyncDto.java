package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PhotoSyncDto {

    private String id;
    private String filename;
    private String caption;
    private String createdAt;
    private String previewUrl;

    public PhotoSyncDto() {
    }

}