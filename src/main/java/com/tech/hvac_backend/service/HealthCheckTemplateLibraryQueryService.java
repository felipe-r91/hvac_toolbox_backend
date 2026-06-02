package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryItemResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryTaskResponse;
import com.tech.hvac_backend.entity.HealthCheckTemplateEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateTaskEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateVersionEntity;
import com.tech.hvac_backend.repository.HealthCheckTemplateRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateTaskRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class HealthCheckTemplateLibraryQueryService {

    private static final String TEMPLATE_TYPE = "HEALTH_CHECK";

    private final HealthCheckTemplateRepository templateRepository;
    private final HealthCheckTemplateVersionRepository versionRepository;
    private final HealthCheckTemplateTaskRepository taskRepository;

    public HealthCheckTemplateLibraryQueryService(
            HealthCheckTemplateRepository templateRepository,
            HealthCheckTemplateVersionRepository versionRepository,
            HealthCheckTemplateTaskRepository taskRepository
    ) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.taskRepository = taskRepository;
    }

    public MaintenanceTemplateLibraryResponse getTemplateLibrary() {
        List<MaintenanceTemplateLibraryItemResponse> templates = templateRepository.findAll()
                .stream()
                .filter(template -> Boolean.TRUE.equals(template.getIsActive()))
                .map(this::mapTemplate)
                .filter(Objects::nonNull)
                .toList();

        return new MaintenanceTemplateLibraryResponse(templates);
    }

    private MaintenanceTemplateLibraryItemResponse mapTemplate(HealthCheckTemplateEntity template) {
        HealthCheckTemplateVersionEntity version = versionRepository
                .findFirstByTemplateIdAndIsPublishedTrueOrderByVersionNumberDesc(template.getId())
                .orElse(null);

        if (version == null) {
            return null;
        }

        List<MaintenanceTemplateLibraryTaskResponse> tasks = taskRepository
                .findByTemplateVersionIdOrderBySortOrderAsc(version.getId())
                .stream()
                .map(this::mapTask)
                .toList();

        return new MaintenanceTemplateLibraryItemResponse(
                template.getCode(),
                template.getName(),
                TEMPLATE_TYPE,
                version.getId(),
                version.getVersionNumber(),
                tasks
        );
    }

    private MaintenanceTemplateLibraryTaskResponse mapTask(HealthCheckTemplateTaskEntity entity) {
        return new MaintenanceTemplateLibraryTaskResponse(
                entity.getTaskCode(),
                entity.getCategory(),
                entity.getTaskName(),
                entity.getTool(),
                entity.getDefaultUnit(),
                entity.getIsRequired(),
                entity.getMeasurable(),
                entity.getPhotoRequiredOnFault(),
                entity.getPhotoRequiredOnAttention()
        );
    }
}
