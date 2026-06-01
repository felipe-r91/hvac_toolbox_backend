package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineHealthCheckResponse;
import com.tech.hvac_backend.dto.response.MachinePlanMachineResponse;
import com.tech.hvac_backend.dto.response.MaintenancePlanTaskResponse;
import com.tech.hvac_backend.entity.HealthCheckTaskEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateVersionEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.HealthCheckTaskRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateVersionRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthCheckQueryService {

    private static final String HEALTH_CHECK_TEMPLATE_CODE = "HEALTH_CHECK";

    private final MachineRepository machineRepository;
    private final HealthCheckTemplateRepository healthCheckTemplateRepository;
    private final HealthCheckTemplateVersionRepository healthCheckTemplateVersionRepository;
    private final HealthCheckTaskRepository healthCheckTaskRepository;

    public HealthCheckQueryService(
            MachineRepository machineRepository,
            HealthCheckTemplateRepository healthCheckTemplateRepository,
            HealthCheckTemplateVersionRepository healthCheckTemplateVersionRepository,
            HealthCheckTaskRepository healthCheckTaskRepository
    ) {
        this.machineRepository = machineRepository;
        this.healthCheckTemplateRepository = healthCheckTemplateRepository;
        this.healthCheckTemplateVersionRepository = healthCheckTemplateVersionRepository;
        this.healthCheckTaskRepository = healthCheckTaskRepository;
    }

    public MachineHealthCheckResponse getHealthCheck(String machineId) {
        MachineEntity machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));

        HealthCheckTemplateVersionEntity templateVersion = getPublishedHealthCheckVersion();
        List<MaintenancePlanTaskResponse> tasks = getPublishedHealthCheckTasks(templateVersion)
                .stream()
                .map(this::mapTask)
                .toList();

        return new MachineHealthCheckResponse(
                mapMachine(machine),
                HEALTH_CHECK_TEMPLATE_CODE,
                templateVersion == null ? null : templateVersion.getId(),
                templateVersion == null ? null : templateVersion.getVersionNumber(),
                tasks
        );
    }

    private HealthCheckTemplateVersionEntity getPublishedHealthCheckVersion() {
        HealthCheckTemplateEntity template = healthCheckTemplateRepository
                .findByCode(HEALTH_CHECK_TEMPLATE_CODE)
                .filter(entity -> Boolean.TRUE.equals(entity.getIsActive()))
                .orElse(null);

        if (template == null) {
            return null;
        }

        return healthCheckTemplateVersionRepository
                .findFirstByTemplateIdAndIsPublishedTrueOrderByVersionNumberDesc(template.getId())
                .orElse(null);
    }

    private List<HealthCheckTaskEntity> getPublishedHealthCheckTasks(HealthCheckTemplateVersionEntity templateVersion) {
        if (templateVersion == null) {
            return List.of();
        }

        return healthCheckTaskRepository.findByTemplateVersionIdOrderBySortOrderAsc(templateVersion.getId());
    }

    private MachinePlanMachineResponse mapMachine(MachineEntity machine) {
        return new MachinePlanMachineResponse(
                machine.getId(),
                machine.getVesselId(),
                machine.getLocation(),
                machine.getTag(),
                machine.getModel(),
                machine.getSerialNumber(),
                machine.getType(),
                machine.getStarterType(),
                machine.getRefrigerant(),
                machine.getOilType(),
                machine.getControlSystem(),
                machine.getSoftwareVersion(),
                machine.getCompressorType(),
                machine.getMfg(),
                machine.getMachineTemplateVersionId(),
                machine.getStarterTemplateVersionId()
        );
    }

    private MaintenancePlanTaskResponse mapTask(HealthCheckTaskEntity entity) {
        return new MaintenancePlanTaskResponse(
                entity.getId(),
                entity.getCategory(),
                entity.getTaskName(),
                null,
                false,
                defaultString(entity.getStatus(), "pending"),
                defaultString(entity.getNotes(), ""),
                defaultString(entity.getMeasuredValue(), ""),
                defaultString(entity.getUnit(), ""),
                true,
                hasText(entity.getMeasuredValue()) || hasText(entity.getUnit()),
                defaultBoolean(entity.getPhotoRequiredOnFault()),
                defaultBoolean(entity.getPhotoRequiredOnAttention())
        );
    }

    private String defaultString(String value, String defaultValue) {
        return hasText(value) ? value : defaultValue;
    }

    private Boolean defaultBoolean(Boolean value) {
        return value == null || value;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank() && !"null".equalsIgnoreCase(value.trim());
    }
}
