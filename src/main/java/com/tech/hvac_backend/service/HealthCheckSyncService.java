package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.PreventiveTaskDto;
import com.tech.hvac_backend.dto.sync.HealthCheckSyncRequest;
import com.tech.hvac_backend.entity.HealthCheckReportEntity;
import com.tech.hvac_backend.entity.HealthCheckReportTaskEntity;
import com.tech.hvac_backend.repository.HealthCheckReportRepository;
import com.tech.hvac_backend.repository.HealthCheckReportTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HealthCheckSyncService {

    public static final String REPORT_CATEGORY = "health_check";

    private final HealthCheckReportRepository healthCheckReportRepository;
    private final HealthCheckReportTaskRepository healthCheckReportTaskRepository;

    public HealthCheckSyncService(
            HealthCheckReportRepository healthCheckReportRepository,
            HealthCheckReportTaskRepository healthCheckReportTaskRepository
    ) {
        this.healthCheckReportRepository = healthCheckReportRepository;
        this.healthCheckReportTaskRepository = healthCheckReportTaskRepository;
    }

    @Transactional
    public boolean syncHealthCheckReport(HealthCheckSyncRequest request) {
        validateRequest(request);

        if (healthCheckReportRepository.existsById(request.getId())) {
            return false;
        }

        HealthCheckReportEntity reportEntity = mapReport(request);
        healthCheckReportRepository.save(reportEntity);

        List<HealthCheckReportTaskEntity> taskEntities = new ArrayList<>();
        for (PreventiveTaskDto taskDto : request.getTasks()) {
            taskEntities.add(mapTask(request.getId(), taskDto));
        }

        healthCheckReportTaskRepository.saveAll(taskEntities);

        return true;
    }

    private void validateRequest(HealthCheckSyncRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        if (isBlank(request.getId())) {
            throw new IllegalArgumentException("Report id is required.");
        }

        if (isBlank(request.getVesselId())) {
            throw new IllegalArgumentException("Vessel id is required.");
        }

        if (isBlank(request.getMachineId())) {
            throw new IllegalArgumentException("Machine id is required.");
        }

        if (isBlank(request.getCompletedAt())) {
            throw new IllegalArgumentException("CompletedAt is required.");
        }

        if (request.getTasks() == null || request.getTasks().isEmpty()) {
            throw new IllegalArgumentException("At least one task is required.");
        }
    }

    private HealthCheckReportEntity mapReport(HealthCheckSyncRequest request) {
        HealthCheckReportEntity entity = new HealthCheckReportEntity();
        entity.setId(request.getId());
        entity.setVesselId(request.getVesselId());
        entity.setVesselName(request.getVesselName());
        entity.setMachineId(request.getMachineId());
        entity.setMachineTag(request.getMachineTag());
        entity.setMachineModel(request.getMachineModel());
        entity.setMachineSerialNumber(request.getMachineSerialNumber());
        entity.setMachineType(request.getMachineType());
        entity.setMachineLocation(request.getMachineLocation());
        entity.setMachineStarterType(request.getMachineStarterType());
        entity.setCompletedAt(request.getCompletedAt());
        entity.setOverallStatus(resolveOverallStatus(request));
        entity.setDowntimeReason(request.getDowntimeReason());
        entity.setFailureComponent(request.getFailureComponent());
        entity.setFailureMode(request.getFailureMode());
        entity.setFailureCode(request.getFailureCode());
        entity.setFailureNotes(request.getFailureNotes());
        entity.setFaultCount(defaultInt(request.getFaultCount()));
        entity.setSkippedCount(defaultInt(request.getSkippedCount()));
        entity.setSynced(Boolean.TRUE);
        return entity;
    }

    private HealthCheckReportTaskEntity mapTask(String reportId, PreventiveTaskDto dto) {
        HealthCheckReportTaskEntity entity = new HealthCheckReportTaskEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setReportId(reportId);
        entity.setTaskTemplateId(dto.getId());
        entity.setCategory(dto.getCategory());
        entity.setTaskName(dto.getTask());
        entity.setTool(dto.getTool());
        entity.setChecked(Boolean.TRUE.equals(dto.getChecked()));
        entity.setStatus(dto.getStatus());
        entity.setNotes(dto.getNotes());
        entity.setMeasuredValue(dto.getMeasuredValue());
        entity.setUnit(dto.getUnit());
        entity.setPhotoIds(dto.getPhotoIds());
        entity.setCompletedAt(dto.getCompletedAt());
        return entity;
    }

    private String resolveOverallStatus(HealthCheckSyncRequest request) {
        if (defaultInt(request.getFaultCount()) > 0) {
            return "down";
        }

        boolean hasFaultTask = request.getTasks().stream()
                .anyMatch(task -> "fault".equalsIgnoreCase(task.getStatus()));

        return hasFaultTask ? "down" : "online";
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
