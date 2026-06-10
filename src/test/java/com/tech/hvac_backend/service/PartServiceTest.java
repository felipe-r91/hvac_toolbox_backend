package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.request.PartRequest;
import com.tech.hvac_backend.dto.response.PartResponse;
import com.tech.hvac_backend.entity.PartEntity;
import com.tech.hvac_backend.repository.PartRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartServiceTest {

    private final PartRepositoryStub repositoryStub = new PartRepositoryStub();
    private final PartService service = new PartService(repositoryStub.createProxy());

    @Test
    void createPartTrimsFieldsAndRemovesDuplicateMachineModels() {
        PartRequest request = request();
        request.setMachinesModelHavingIt(List.of(" YK-01 ", "YK-01", "YC-02"));

        PartResponse response = service.createPart(request);

        assertThat(response.getId()).isNotBlank();
        assertThat(response.getJciPartNumber()).isEqualTo("JCI-123");
        assertThat(response.getManufacturerModel()).isEqualTo("Model A");
        assertThat(response.getMachinesModelHavingIt()).containsExactly("YK-01", "YC-02");
        assertThat(response.getPartPhotoId()).isNull();
        assertThat(response.getPartPhotoPreviewUrl()).isNull();
        assertThat(repositoryStub.savedPart.getJciPartNumber()).isEqualTo("JCI-123");
    }

    @Test
    void createPartRejectsDuplicateJciPartNumber() {
        PartRequest request = request();
        repositoryStub.duplicatePartNumber = true;

        assertThatThrownBy(() -> service.createPart(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Part already exists: JCI-123");
    }

    @Test
    void deletePartRequiresAnExistingPart() {
        PartEntity part = new PartEntity();
        part.setId("part_1");
        repositoryStub.existingPart = part;

        service.deletePart("part_1");

        assertThat(repositoryStub.deletedPart).isSameAs(part);
    }

    private PartRequest request() {
        PartRequest request = new PartRequest();
        request.setJciPartNumber(" JCI-123 ");
        request.setManufacturerModel(" Model A ");
        request.setManufacturerCode(" MFG-01 ");
        request.setTag(" Compressor ");
        request.setMachinesModelHavingIt(List.of("YK-01"));
        request.setDescription(" Replacement compressor part. ");
        return request;
    }

    private static class PartRepositoryStub implements InvocationHandler {

        private boolean duplicatePartNumber;
        private PartEntity existingPart;
        private PartEntity savedPart;
        private PartEntity deletedPart;

        private PartRepository createProxy() {
            return (PartRepository) Proxy.newProxyInstance(
                    PartRepository.class.getClassLoader(),
                    new Class<?>[]{PartRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "existsByJciPartNumberIgnoreCase" -> duplicatePartNumber;
                case "findById" -> Optional.ofNullable(existingPart);
                case "save" -> {
                    savedPart = (PartEntity) args[0];
                    yield savedPart;
                }
                case "delete" -> {
                    deletedPart = (PartEntity) args[0];
                    yield null;
                }
                default -> throw new UnsupportedOperationException(method.getName());
            };
        }
    }
}
