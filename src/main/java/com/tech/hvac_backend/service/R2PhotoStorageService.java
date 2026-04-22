package com.tech.hvac_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "r2")
public class R2PhotoStorageService implements PhotoStorageService {

    private final S3Client s3Client;
    private final String bucket;

    public R2PhotoStorageService(
            @Value("${r2.endpoint}") String endpoint,
            @Value("${r2.access-key}") String accessKey,
            @Value("${r2.secret-key}") String secretKey,
            @Value("${r2.bucket}") String bucket
    ) {
        this.bucket = bucket;

        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();

        System.out.println("R2PhotoStorageService active");
        System.out.println("R2 bucket: " + bucket);
        System.out.println("R2 endpoint: " + endpoint);
    }

    @Override
    public String storePhoto(MultipartFile file, String ownerType, String ownerId) {
        try {
            String extension = extractExtension(file.getOriginalFilename());
            String key =
                    "photos/" +
                            sanitizePathPart(ownerType) + "/" +
                            sanitizePathPart(ownerId) + "/" +
                            System.currentTimeMillis() + "-" + UUID.randomUUID() + extension;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo to R2.", e);
        }
    }

    @Override
    public Resource loadPhotoAsResource(String storageKey) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(storageKey)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(request);

            return new ByteArrayResource(objectBytes.asByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load photo from R2.", e);
        }
    }

    @Override
    public String detectContentType(Resource resource, String filename) {
        String lower = filename == null ? "" : filename.toLowerCase();

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

        return "application/octet-stream";
    }

    @Override
    public String buildPreviewUrl(String photoId) {
        return "/api/photos/" + photoId;
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }

        return filename.substring(filename.lastIndexOf('.'));
    }

    private String sanitizePathPart(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}