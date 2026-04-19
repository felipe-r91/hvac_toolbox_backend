package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.sync.PreventiveSyncRequest;
import com.tech.hvac_backend.dto.PreventiveTaskDto;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.PreventiveReportTaskEntity;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import com.tech.hvac_backend.repository.PreventiveReportTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PreventiveSyncService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final PreventiveReportTaskRepository preventiveReportTaskRepository;

    public PreventiveSyncService(
            PreventiveReportRepository preventiveReportRepository,
            PreventiveReportTaskRepository preventiveReportTaskRepository
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.preventiveReportTaskRepository = preventiveReportTaskRepository;
    }

    @Transactional
    public boolean syncPreventiveReport(PreventiveSyncRequest request) {
        validateRequest(request);

        if (preventiveReportRepository.existsById(request.getId())) {
            return false;
        }

        PreventiveReportEntity reportEntity = mapReport(request);
        preventiveReportRepository.save(reportEntity);

        List<PreventiveReportTaskEntity> taskEntities = new ArrayList<>();
        for (PreventiveTaskDto taskDto : request.getTasks()) {
            taskEntities.add(mapTask(request.getId(), taskDto));
        }

        preventiveReportTaskRepository.saveAll(taskEntities);

        return true;
    }

    private void validateRequest(PreventiveSyncRequest request) {
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

    private PreventiveReportEntity mapReport(PreventiveSyncRequest request) {
        PreventiveReportEntity entity = new PreventiveReportEntity();
        entity.setId(request.getId());
        entity.setVesselId(request.getVesselId());
        entity.setVesselName(request.getVesselName());
        entity.setMachineId(request.getMachineId());
        entity.setMachineTag(request.getMachineTag());
        entity.setMachineModel(request.getMachineModel());
        entity.setMachineType(request.getMachineType());
        entity.setMachineLocation(request.getMachineLocation());
        entity.setMachineStarterType(request.getMachineStarterType());
        entity.setCompletedAt(request.getCompletedAt());
        entity.setOverallStatus(request.getOverallStatus());
        entity.setDowntimeReason(request.getDowntimeReason());
        entity.setFailureComponent(request.getFailureComponent());
        entity.setFailureMode(request.getFailureMode());
        entity.setFailureCode(request.getFailureCode());
        entity.setLinkedCorrectiveDraftId(request.getLinkedCorrectiveDraftId());
        entity.setFailureNotes(request.getFailureNotes());
        entity.setFaultCount(defaultInt(request.getFaultCount()));
        entity.setSkippedCount(defaultInt(request.getSkippedCount()));
        entity.setSynced(Boolean.TRUE);
        return entity;
    }

    private PreventiveReportTaskEntity mapTask(String reportId, PreventiveTaskDto dto) {
        PreventiveReportTaskEntity entity = new PreventiveReportTaskEntity();
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
        entity.setCompletedAt(dto.getCompletedAt());
        return entity;
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}