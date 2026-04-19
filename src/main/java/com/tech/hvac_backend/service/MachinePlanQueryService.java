package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachinePlanMachineResponse;
import com.tech.hvac_backend.dto.response.MachinePlanResponse;
import com.tech.hvac_backend.dto.response.MaintenancePlanTaskResponse;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.MaintenanceTemplateTaskEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.MaintenanceTemplateTaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MachinePlanQueryService {

    private final MachineRepository machineRepository;
    private final MaintenanceTemplateTaskRepository maintenanceTemplateTaskRepository;

    public MachinePlanQueryService(
            MachineRepository machineRepository,
            MaintenanceTemplateTaskRepository maintenanceTemplateTaskRepository
    ) {
        this.machineRepository = machineRepository;
        this.maintenanceTemplateTaskRepository = maintenanceTemplateTaskRepository;
    }

    public MachinePlanResponse getMachinePlan(String machineId) {
        MachineEntity machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));

        List<MaintenancePlanTaskResponse> tasks = new ArrayList<>();

        if (machine.getMachineTemplateVersionId() != null && !machine.getMachineTemplateVersionId().isBlank()) {
            tasks.addAll(
                    maintenanceTemplateTaskRepository
                            .findByTemplateVersionIdOrderBySortOrderAsc(machine.getMachineTemplateVersionId())
                            .stream()
                            .map(this::mapTask)
                            .toList()
            );
        }

        if (machine.getStarterTemplateVersionId() != null && !machine.getStarterTemplateVersionId().isBlank()) {
            tasks.addAll(
                    maintenanceTemplateTaskRepository
                            .findByTemplateVersionIdOrderBySortOrderAsc(machine.getStarterTemplateVersionId())
                            .stream()
                            .map(this::mapTask)
                            .toList()
            );
        }

        MachinePlanMachineResponse machineResponse = new MachinePlanMachineResponse(
                machine.getId(),
                machine.getVesselId(),
                machine.getLocation(),
                machine.getTag(),
                machine.getModel(),
                machine.getSerialNumber(),
                machine.getType(),
                machine.getStarterType(),
                machine.getMachineTemplateVersionId(),
                machine.getStarterTemplateVersionId()
        );

        return new MachinePlanResponse(machineResponse, tasks);
    }

    private MaintenancePlanTaskResponse mapTask(MaintenanceTemplateTaskEntity entity) {
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
                entity.getPhotoRequiredOnFault(),
                entity.getPhotoRequiredOnAttention()
        );
    }
}