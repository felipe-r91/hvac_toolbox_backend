package com.tech.hvac_backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorageService {

    String storePhoto(MultipartFile file, String ownerType, String ownerId) throws Exception;

    Resource loadPhotoAsResource(String storageKey) throws Exception;

    String detectContentType(Resource resource, String filename);

    String buildPreviewUrl(String photoId);
}