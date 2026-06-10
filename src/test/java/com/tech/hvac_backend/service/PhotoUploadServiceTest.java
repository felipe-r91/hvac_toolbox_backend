package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.PartEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PartRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhotoUploadServiceTest {

    private final PhotoStorageServiceStub storageService = new PhotoStorageServiceStub();
    private final RepositoryStub repositoryStub = new RepositoryStub();
    private final PhotoUploadService service = new PhotoUploadService(
            storageService,
            repositoryStub.proxy(PhotoRecordRepository.class),
            repositoryStub.proxy(MachineRepository.class),
            repositoryStub.proxy(PartRepository.class)
    );

    @Test
    void uploadPartPictureStoresFileMetadataAndUpdatesPart() throws Exception {
        PartEntity part = new PartEntity();
        part.setId("part_1");
        repositoryStub.part = part;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "part.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        PhotoRecordEntity photo = service.uploadPhoto(
                PhotoOwnerType.PART_PROFILE,
                "part_1",
                null,
                "part_1",
                null,
                "",
                file
        );

        assertThat(photo.getOwnerType()).isEqualTo(PhotoOwnerType.PART_PROFILE);
        assertThat(photo.getOwnerId()).isEqualTo("part_1");
        assertThat(photo.getPartId()).isEqualTo("part_1");
        assertThat(photo.getMachineId()).isNull();
        assertThat(photo.getStorageKey()).isEqualTo("photos/PART_PROFILE/part_1/photo.jpg");
        assertThat(part.getPartPhotoId()).isEqualTo(photo.getId());
        assertThat(part.getPartPhotoPreviewUrl()).isEqualTo(photo.getPreviewUrl());
        assertThat(repositoryStub.savedPhoto).isSameAs(photo);
        assertThat(repositoryStub.savedPart).isSameAs(part);
    }

    @Test
    void uploadPartPictureRejectsMachineOwnershipFields() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "part.jpg",
                "image/jpeg",
                new byte[]{1}
        );

        assertThatThrownBy(() -> service.uploadPhoto(
                PhotoOwnerType.PART_PROFILE,
                "part_1",
                "machine_1",
                "part_1",
                null,
                "",
                file
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("machineId must not be sent for PART_PROFILE photos.");
    }

    private static class PhotoStorageServiceStub implements PhotoStorageService {

        @Override
        public String storePhoto(MultipartFile file, String ownerType, String ownerId) {
            return "photos/%s/%s/photo.jpg".formatted(ownerType, ownerId);
        }

        @Override
        public Resource loadPhotoAsResource(String storageKey) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String detectContentType(Resource resource, String filename) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String buildPreviewUrl(String photoId) {
            return "/api/photos/" + photoId;
        }
    }

    private static class RepositoryStub implements InvocationHandler {

        private PartEntity part;
        private PartEntity savedPart;
        private PhotoRecordEntity savedPhoto;

        @SuppressWarnings("unchecked")
        private <T> T proxy(Class<T> repositoryType) {
            return (T) Proxy.newProxyInstance(
                    repositoryType.getClassLoader(),
                    new Class<?>[]{repositoryType},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Class<?> repositoryType = proxy.getClass().getInterfaces()[0];

            if (repositoryType == PartRepository.class) {
                return switch (method.getName()) {
                    case "findById" -> Optional.ofNullable(part);
                    case "save" -> {
                        savedPart = (PartEntity) args[0];
                        yield savedPart;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                };
            }

            if (repositoryType == PhotoRecordRepository.class && method.getName().equals("save")) {
                savedPhoto = (PhotoRecordEntity) args[0];
                return savedPhoto;
            }

            throw new UnsupportedOperationException(method.getName());
        }
    }
}
