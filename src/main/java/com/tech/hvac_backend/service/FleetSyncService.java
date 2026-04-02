package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineResponse;
import com.tech.hvac_backend.dto.response.VesselResponse;
import com.tech.hvac_backend.dto.sync.FleetSyncRequest;
import com.tech.hvac_backend.dto.sync.MachineSyncDto;
import com.tech.hvac_backend.dto.sync.VesselSyncDto;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FleetSyncService {

    private final VesselRepository vesselRepository;
    private final MachineRepository machineRepository;

    public FleetSyncService(
            VesselRepository vesselRepository,
            MachineRepository machineRepository
    ) {
        this.vesselRepository = vesselRepository;
        this.machineRepository = machineRepository;
    }

    @Transactional
    public void syncFleet(FleetSyncRequest request) {
        if (request == null || request.getVessels() == null) {
            throw new IllegalArgumentException("Fleet request cannot be null.");
        }

        for (VesselSyncDto vesselDto : request.getVessels()) {
            validateVessel(vesselDto);

            VesselEntity vessel = new VesselEntity();
            vessel.setId(vesselDto.getId());
            vessel.setName(vesselDto.getName());
            vessel.setImoNumber(vesselDto.getImoNumber());
            vessel.setDescription(vesselDto.getDescription());
            vesselRepository.save(vessel);

            if (vesselDto.getMachines() == null) {
                continue;
            }

            for (MachineSyncDto machineDto : vesselDto.getMachines()) {
                validateMachine(machineDto);

                MachineEntity machine = new MachineEntity();
                machine.setId(machineDto.getId());
                machine.setVesselId(vesselDto.getId());
                machine.setLocation(machineDto.getLocation());
                machine.setTag(machineDto.getTag());
                machine.setModel(machineDto.getModel());
                machine.setSerialNumber(machineDto.getSerialNumber());
                machine.setType(machineDto.getType());
                machine.setStarterType(machineDto.getStarterType());
                machineRepository.save(machine);
            }
        }
    }

    public List<VesselResponse> getAllVessels() {
        return vesselRepository.findAllByOrderByNameAsc()
                .stream()
                .map(vessel -> new VesselResponse(
                        vessel.getId(),
                        vessel.getName(),
                        vessel.getImoNumber(),
                        vessel.getDescription(),
                        machineRepository.findByVesselIdOrderByTagAsc(vessel.getId())
                                .stream()
                                .map(this::mapMachine)
                                .toList()
                ))
                .toList();
    }

    public List<MachineResponse> getAllMachines() {
        return machineRepository.findAllByOrderByTagAsc()
                .stream()
                .map(this::mapMachine)
                .toList();
    }

    private MachineResponse mapMachine(MachineEntity entity) {
        return new MachineResponse(
                entity.getId(),
                entity.getVesselId(),
                entity.getLocation(),
                entity.getTag(),
                entity.getModel(),
                entity.getSerialNumber(),
                entity.getType(),
                entity.getStarterType()
        );
    }

    private void validateVessel(VesselSyncDto vessel) {
        if (isBlank(vessel.getId())) {
            throw new IllegalArgumentException("Vessel id is required.");
        }
        if (isBlank(vessel.getName())) {
            throw new IllegalArgumentException("Vessel name is required.");
        }
        if (isBlank(vessel.getImoNumber())) {
            throw new IllegalArgumentException("Vessel IMO number is required.");
        }
    }

    private void validateMachine(MachineSyncDto machine) {
        if (isBlank(machine.getId())) {
            throw new IllegalArgumentException("Machine id is required.");
        }
        if (isBlank(machine.getLocation())) {
            throw new IllegalArgumentException("Machine location is required.");
        }
        if (isBlank(machine.getTag())) {
            throw new IllegalArgumentException("Machine tag is required.");
        }
        if (isBlank(machine.getModel())) {
            throw new IllegalArgumentException("Machine model is required.");
        }
        if (isBlank(machine.getSerialNumber())) {
            throw new IllegalArgumentException("Machine serial number is required.");
        }
        if (isBlank(machine.getType())) {
            throw new IllegalArgumentException("Machine type is required.");
        }
        if (isBlank(machine.getStarterType())) {
            throw new IllegalArgumentException("Machine starter type is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}