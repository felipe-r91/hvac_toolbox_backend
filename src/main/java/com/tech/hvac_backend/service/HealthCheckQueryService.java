package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineHealthCheckResponse;
import com.tech.hvac_backend.dto.response.MachinePlanMachineResponse;
import com.tech.hvac_backend.dto.response.MaintenancePlanTaskResponse;
import com.tech.hvac_backend.entity.HealthCheckTemplateEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateTaskEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateVersionEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.HealthCheckTemplateRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateTaskRepository;
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
    private final HealthCheckTemplateTaskRepository healthCheckTemplateTaskRepository;

    public HealthCheckQueryService(
            MachineRepository machineRepository,
            HealthCheckTemplateRepository healthCheckTemplateRepository,
            HealthCheckTemplateVersionRepository healthCheckTemplateVersionRepository,
            HealthCheckTemplateTaskRepository healthCheckTemplateTaskRepository
    ) {
        this.machineRepository = machineRepository;
        this.healthCheckTemplateRepository = healthCheckTemplateRepository;
        this.healthCheckTemplateVersionRepository = healthCheckTemplateVersionRepository;
        this.healthCheckTemplateTaskRepository = healthCheckTemplateTaskRepository;
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

    private List<HealthCheckTemplateTaskEntity> getPublishedHealthCheckTasks(HealthCheckTemplateVersionEntity templateVersion) {
        if (templateVersion == null) {
            return List.of();
        }

        return healthCheckTemplateTaskRepository.findByTemplateVersionIdOrderBySortOrderAsc(templateVersion.getId());
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

    private MaintenancePlanTaskResponse mapTask(HealthCheckTemplateTaskEntity entity) {
        return new MaintenancePlanTaskResponse(
                entity.getTaskCode(),
                entity.getCategory(),
                entity.getTaskName(),
                entity.getTool(),
                false,
                "pending",
                "",
                "",
                entity.getDefaultUnit(),
                entity.getIsRequired(),
                entity.getMeasurable(),
                defaultBoolean(entity.getPhotoRequiredOnFault()),
                defaultBoolean(entity.getPhotoRequiredOnAttention())
        );
    }

    private Boolean defaultBoolean(Boolean value) {
        return value == null || value;
    }
}
