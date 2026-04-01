package com.tech.hvac_backend.service;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("prod")
public class R2PhotoStorageService implements PhotoStorageService {

    @Override
    public String storePhoto(MultipartFile file, String draftId) {
        throw new UnsupportedOperationException("R2 storage not implemented yet.");
    }

    @Override
    public Resource loadPhotoAsResource(String storageKey) {
        throw new UnsupportedOperationException("R2 storage not implemented yet.");
    }

    @Override
    public String detectContentType(Resource resource, String filename) {
        return "application/octet-stream";
    }

    @Override
    public String buildPreviewUrl(String photoId) {
        return "/api/photos/" + photoId;
    }
}