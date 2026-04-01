package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class CorrectivePhotoDetailResponse {

    private String id;
    private String filename;
    private String caption;
    private String createdAt;
    private String previewUrl;

    public CorrectivePhotoDetailResponse(
            String id,
            String filename,
            String caption,
            String createdAt,
            String previewUrl
    ) {
        this.id = id;
        this.filename = filename;
        this.caption = caption;
        this.createdAt = createdAt;
        this.previewUrl = previewUrl;
    }

}