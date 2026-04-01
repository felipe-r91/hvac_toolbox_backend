package com.tech.hvac_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Profile({"default", "local"})
public class LocalPhotoStorageService implements PhotoStorageService {

    private final Path uploadRoot;

    public LocalPhotoStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String storePhoto(MultipartFile file, String draftId) throws IOException {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String generatedFilename = UUID.randomUUID() + extension;

        String storageKey = Paths.get("corrective", draftId, generatedFilename)
                .toString()
                .replace("\\", "/");

        Path destination = uploadRoot.resolve(storageKey).normalize();
        Files.createDirectories(destination.getParent());

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return storageKey;
    }

    @Override
    public Resource loadPhotoAsResource(String storageKey) throws MalformedURLException {
        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("Storage key cannot be null or blank.");
        }

        Path path = uploadRoot.resolve(storageKey).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("Photo file not found or unreadable: " + storageKey);
        }

        return resource;
    }

    @Override
    public String detectContentType(Resource resource, String filename) {
        try {
            Path path = Paths.get(resource.getFile().getAbsolutePath());
            String detected = Files.probeContentType(path);

            if (detected != null && !detected.isBlank()) {
                return detected;
            }
        } catch (IOException ignored) {
        }

        return inferContentTypeFromFilename(filename);
    }

    @Override
    public String buildPreviewUrl(String photoId) {
        return "/api/photos/" + photoId;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return ".jpg";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return ".jpg";
        }

        return filename.substring(dotIndex);
    }

    private String inferContentTypeFromFilename(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }

        String lower = filename.toLowerCase();

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".bmp")) {
            return "image/bmp";
        }

        return "application/octet-stream";
    }
}