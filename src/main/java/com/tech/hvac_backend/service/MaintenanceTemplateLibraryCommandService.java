package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.request.TemplatePlanRequest;
import com.tech.hvac_backend.dto.request.TemplatePlanTaskRequest;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryItemResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryTaskResponse;
import com.tech.hvac_backend.entity.MaintenanceTemplateEntity;
import com.tech.hvac_backend.entity.MaintenanceTemplateTaskEntity;
import com.tech.hvac_backend.entity.MaintenanceTemplateVersionEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.MaintenanceTemplateRepository;
import com.tech.hvac_backend.repository.MaintenanceTemplateTaskRepository;
import com.tech.hvac_backend.repository.MaintenanceTemplateVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MaintenanceTemplateLibraryCommandService {

    private static final Set<String> ALLOWED_TEMPLATE_TYPES = Set.of("MACHINE", "STARTER");

    private final MaintenanceTemplateRepository templateRepository;
    private final MaintenanceTemplateVersionRepository versionRepository;
    private final MaintenanceTemplateTaskRepository taskRepository;

    public MaintenanceTemplateLibraryCommandService(
            MaintenanceTemplateRepository templateRepository,
            MaintenanceTemplateVersionRepository versionRepository,
            MaintenanceTemplateTaskRepository taskRepository
    ) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public MaintenanceTemplateLibraryItemResponse createTemplate(TemplatePlanRequest request) {
        String code = requiredText(request.getCode(), "Plan code is required.");
        if (templateRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Plan already exists: " + code);
        }

        MaintenanceTemplateEntity template = new MaintenanceTemplateEntity();
        template.setId(UUID.randomUUID().toString());
        template.setCode(code);
        template.setName(requiredText(request.getName(), "Plan name is required."));
        template.setTemplateType(validateTemplateType(request.getTemplateType()));
        template.setIsActive(true);
        templateRepository.save(template);

        return publishVersion(template, request, 1);
    }

    @Transactional
    public MaintenanceTemplateLibraryItemResponse updateTemplate(String code, TemplatePlanRequest request) {
        MaintenanceTemplateEntity template = findTemplate(code);
        validateUnchangedCode(code, request.getCode());

        template.setName(requiredText(request.getName(), "Plan name is required."));
        template.setTemplateType(validateTemplateType(request.getTemplateType()));
        template.setIsActive(true);
        templateRepository.save(template);

        Integer nextVersionNumber = versionRepository.findByTemplateIdOrderByVersionNumberDesc(template.getId())
                .stream()
                .findFirst()
                .map(version -> version.getVersionNumber() + 1)
                .orElse(1);

        return publishVersion(template, request, nextVersionNumber);
    }

    @Transactional
    public void deleteTemplate(String code) {
        MaintenanceTemplateEntity template = findTemplate(code);
        template.setIsActive(false);
        templateRepository.save(template);
    }

    private MaintenanceTemplateEntity findTemplate(String code) {
        return templateRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance plan not found: " + code));
    }

    private MaintenanceTemplateLibraryItemResponse publishVersion(
            MaintenanceTemplateEntity template,
            TemplatePlanRequest request,
            Integer versionNumber
    ) {
        MaintenanceTemplateVersionEntity version = new MaintenanceTemplateVersionEntity();
        version.setId(UUID.randomUUID().toString());
        version.setTemplateId(template.getId());
        version.setVersionNumber(versionNumber);
        version.setNotes(trimToNull(request.getNotes()));
        version.setIsPublished(true);
        versionRepository.save(version);

        List<MaintenanceTemplateTaskEntity> tasks = mapTasks(template.getCode(), version.getId(), request.getTasks());
        taskRepository.saveAll(tasks);

        return new MaintenanceTemplateLibraryItemResponse(
                template.getCode(),
                template.getName(),
                template.getTemplateType(),
                version.getId(),
                version.getVersionNumber(),
                tasks.stream().map(this::mapTask).toList()
        );
    }

    private List<MaintenanceTemplateTaskEntity> mapTasks(
            String templateCode,
            String versionId,
            List<TemplatePlanTaskRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("At least one task is required.");
        }

        Set<String> taskCodes = new HashSet<>();
        List<MaintenanceTemplateTaskEntity> tasks = new ArrayList<>();
        for (int index = 0; index < requests.size(); index++) {
            TemplatePlanTaskRequest request = requests.get(index);
            int sortOrder = index + 1;
            String taskCode = taskCode(request, templateCode, sortOrder);
            if (!taskCodes.add(taskCode)) {
                throw new IllegalArgumentException("Duplicate task code: " + taskCode);
            }

            MaintenanceTemplateTaskEntity entity = new MaintenanceTemplateTaskEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setTemplateVersionId(versionId);
            entity.setTaskCode(taskCode);
            entity.setCategory(requiredText(request.getCategory(), "Task category is required."));
            entity.setTaskName(requiredText(request.getTask(), "Task name is required."));
            entity.setTool(trimToNull(request.getTool()));
            entity.setSortOrder(sortOrder);
            entity.setIsRequired(defaultBoolean(request.getRequired(), true));
            entity.setMeasurable(defaultBoolean(request.getMeasurable(), false));
            entity.setDefaultUnit(trimToNull(request.getUnit()));
            entity.setPhotoRequiredOnFault(defaultBoolean(request.getPhotoRequiredOnFault(), true));
            entity.setPhotoRequiredOnAttention(defaultBoolean(request.getPhotoRequiredOnAttention(), true));
            tasks.add(entity);
        }

        return tasks;
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

    private String validateTemplateType(String templateType) {
        String normalized = requiredText(templateType, "Template type is required.")
                .toUpperCase(Locale.ROOT);
        if (!ALLOWED_TEMPLATE_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Template type must be MACHINE or STARTER.");
        }
        return normalized;
    }

    private void validateUnchangedCode(String existingCode, String requestCode) {
        if (hasText(requestCode) && !existingCode.equals(requestCode.trim())) {
            throw new IllegalArgumentException("Plan code cannot be changed.");
        }
    }

    private String taskCode(TemplatePlanTaskRequest request, String templateCode, int sortOrder) {
        String providedCode = hasText(request.getTaskCode()) ? request.getTaskCode() : request.getId();
        if (hasText(providedCode)) {
            return providedCode.trim();
        }

        String base = templateCode.toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        if (base.isBlank()) {
            base = "TASK";
        }
        if (base.length() > 70) {
            base = base.substring(0, 70);
        }
        return "%s_%03d".formatted(base, sortOrder);
    }

    private Boolean defaultBoolean(Boolean value, Boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    private String requiredText(String value, String message) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
