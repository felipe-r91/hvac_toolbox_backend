package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class PhotoDetailResponse {

    private final String id;
    private final String filename;
    private final String caption;
    private final String createdAt;
    private final String previewUrl;

    public PhotoDetailResponse(
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
