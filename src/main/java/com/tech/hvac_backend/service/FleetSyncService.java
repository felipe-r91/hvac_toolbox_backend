package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineResponse;
import com.tech.hvac_backend.dto.response.VesselResponse;
import com.tech.hvac_backend.dto.sync.FleetSyncRequest;
import com.tech.hvac_backend.dto.sync.MachineSyncDto;
import com.tech.hvac_backend.dto.sync.VesselSyncDto;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
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
            vessel.setVesselType(vesselDto.getVesselType());
            vessel.setOwnerCustomer(vesselDto.getOwnerCustomer());
            vessel.setVesselContact(vesselDto.getVesselContact());
            vessel.setDescription(vesselDto.getDescription());
            vesselRepository.save(vessel);

            if (vesselDto.getMachines() == null) {
                continue;
            }

            for (MachineSyncDto machineDto : vesselDto.getMachines()) {
                validateMachine(machineDto);

                MachineEntity machine = machineRepository.findById(machineDto.getId())
                        .orElseGet(MachineEntity::new);

                machine.setId(machineDto.getId());
                machine.setVesselId(resolveMachineVesselId(vesselDto, machineDto));
                machine.setLocation(machineDto.getLocation());
                machine.setTag(machineDto.getTag());
                machine.setModel(machineDto.getModel());
                machine.setSerialNumber(machineDto.getSerialNumber());
                machine.setType(machineDto.getType());
                machine.setStarterType(machineDto.getStarterType());
                machine.setRefrigerant(machineDto.getRefrigerant());
                machine.setOilType(machineDto.getOilType());
                machine.setControlSystem(machineDto.getControlSystem());
                machine.setSoftwareVersion(machineDto.getSoftwareVersion());
                machine.setCompressorType(machineDto.getCompressorType());
                machine.setMfg(machineDto.getMfg());

                if (machineDto.getMachinePhotoId() != null) {
                    machine.setMachinePhotoId(machineDto.getMachinePhotoId());
                }

                if (machineDto.getMachinePhotoPreviewUrl() != null) {
                    machine.setMachinePhotoPreviewUrl(machineDto.getMachinePhotoPreviewUrl());
                }

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
                        vessel.getVesselType(),
                        vessel.getOwnerCustomer(),
                        vessel.getVesselContact(),
                        vessel.getDescription(),
                        machineRepository.findByVesselIdOrderByTagAsc(vessel.getId())
                                .stream()
                                .map(this::mapMachine)
                                .toList()
                ))
                .toList();
    }

    public VesselResponse getVesselById(String vesselId) {
        VesselEntity vessel = vesselRepository.findById(vesselId)
                .orElseThrow(() -> new ResourceNotFoundException("Vessel not found: " + vesselId));

        return new VesselResponse(
                vessel.getId(),
                vessel.getName(),
                vessel.getImoNumber(),
                vessel.getVesselType(),
                vessel.getOwnerCustomer(),
                vessel.getVesselContact(),
                vessel.getDescription(),
                machineRepository.findByVesselIdOrderByTagAsc(vessel.getId())
                        .stream()
                        .map(this::mapMachine)
                        .toList()
        );
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
                entity.getStarterType(),
                entity.getRefrigerant(),
                entity.getOilType(),
                entity.getControlSystem(),
                entity.getSoftwareVersion(),
                entity.getCompressorType(),
                entity.getMfg(),
                entity.getMachinePhotoId(),
                entity.getMachinePhotoPreviewUrl()
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
        if (isBlank(vessel.getVesselType())) {
            throw new IllegalArgumentException("Vessel type is required.");
        }
        if (isBlank(vessel.getOwnerCustomer())) {
            throw new IllegalArgumentException("Vessel owner customer is required.");
        }
        if (isBlank(vessel.getVesselContact())) {
            throw new IllegalArgumentException("Vessel contact is required.");
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
        if (isBlank(machine.getRefrigerant())) {
            throw new IllegalArgumentException("Machine refrigerant is required.");
        }
        if (isBlank(machine.getOilType())) {
            throw new IllegalArgumentException("Machine oil type is required.");
        }
        if (isBlank(machine.getControlSystem())) {
            throw new IllegalArgumentException("Machine control system is required.");
        }
        if (isBlank(machine.getSoftwareVersion())) {
            throw new IllegalArgumentException("Machine software version is required.");
        }
        if (isBlank(machine.getCompressorType())) {
            throw new IllegalArgumentException("Machine compressor type is required.");
        }
        if (isBlank(machine.getMfg())) {
            throw new IllegalArgumentException("Machine manufacturer is required.");
        }
    }

    private String resolveMachineVesselId(VesselSyncDto vessel, MachineSyncDto machine) {
        if (isBlank(machine.getVesselId())) {
            return vessel.getId();
        }
        if (!machine.getVesselId().equals(vessel.getId())) {
            throw new IllegalArgumentException("Machine vesselId must match parent vessel id.");
        }
        return machine.getVesselId();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
