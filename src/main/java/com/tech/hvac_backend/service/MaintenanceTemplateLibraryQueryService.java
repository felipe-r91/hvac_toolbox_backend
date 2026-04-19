package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryItemResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryTaskResponse;
import com.tech.hvac_backend.entity.MaintenanceTemplateEntity;
import com.tech.hvac_backend.entity.MaintenanceTemplateTaskEntity;
import com.tech.hvac_backend.entity.MaintenanceTemplateVersionEntity;
import com.tech.hvac_backend.repository.MaintenanceTemplateRepository;
import com.tech.hvac_backend.repository.MaintenanceTemplateTaskRepository;
import com.tech.hvac_backend.repository.MaintenanceTemplateVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MaintenanceTemplateLibraryQueryService {

    private final MaintenanceTemplateRepository templateRepository;
    private final MaintenanceTemplateVersionRepository versionRepository;
    private final MaintenanceTemplateTaskRepository taskRepository;

    public MaintenanceTemplateLibraryQueryService(
            MaintenanceTemplateRepository templateRepository,
            MaintenanceTemplateVersionRepository versionRepository,
            MaintenanceTemplateTaskRepository taskRepository
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

    private MaintenanceTemplateLibraryItemResponse mapTemplate(MaintenanceTemplateEntity template) {
        MaintenanceTemplateVersionEntity version = versionRepository
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
                template.getTemplateType(),
                version.getId(),
                version.getVersionNumber(),
                tasks
        );
    }

    private MaintenanceTemplateLibraryTaskResponse mapTask(MaintenanceTemplateTaskEntity entity) {
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