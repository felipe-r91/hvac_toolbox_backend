package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.sync.CfrSyncRequest;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CfrSyncService {

    private final CfrDraftRepository cfrDraftRepository;

    public CfrSyncService(CfrDraftRepository cfrDraftRepository) {
        this.cfrDraftRepository = cfrDraftRepository;
    }

    @Transactional
    public CfrDraftEntity sync(CfrSyncRequest request) {
        CfrDraftEntity entity = cfrDraftRepository.findById(request.getId())
                .orElseGet(CfrDraftEntity::new);

        entity.setId(request.getId());
        entity.setVesselId(request.getVesselId());
        entity.setVesselName(request.getVesselName());
        entity.setVesselType(request.getVesselType());
        entity.setOwnerCustomer(request.getOwnerCustomer());
        entity.setVesselContact(request.getVesselContact());
        entity.setMachineId(request.getMachineId());
        entity.setMachineTag(request.getMachineTag());
        entity.setMachineModel(request.getMachineModel());
        entity.setMachineSerialNumber(request.getMachineSerialNumber());
        entity.setMachineType(request.getMachineType());
        entity.setMachineStarterType(request.getMachineStarterType());
        entity.setMachineLocation(request.getMachineLocation());
        entity.setMachineRefrigerant(request.getMachineRefrigerant());
        entity.setMachineOilType(request.getMachineOilType());
        entity.setMachineControlSystem(request.getMachineControlSystem());
        entity.setMachineSoftwareVersion(request.getMachineSoftwareVersion());
        entity.setMachineCompressorType(request.getMachineCompressorType());
        entity.setMachineMfg(request.getMachineMfg());
        entity.setCreatedAt(request.getCreatedAt());
        entity.setMachineStatus(request.getMachineStatus());
        entity.setReportCategory(
                request.getReportCategory() == null || request.getReportCategory().isBlank()
                        ? "cfr"
                        : request.getReportCategory()
        );

        entity.setFailureComponent(request.getFailureComponent());
        entity.setFailureMode(request.getFailureMode());
        entity.setFailureCode(request.getFailureCode());

        entity.setConditionFound(request.getConditionFound());
        entity.setSymptomsObserved(request.getSymptomsObserved());
        entity.setAlarmsObserved(request.getAlarmsObserved());
        entity.setOperationalImpact(request.getOperationalImpact());
        entity.setPreliminaryDiagnosis(request.getPreliminaryDiagnosis());
        entity.setConfirmedCause(request.getConfirmedCause());
        entity.setRecommendations(request.getRecommendations());
        entity.setFurtherActionRequired(request.getFurtherActionRequired());

        entity.setSynced(true);

        return cfrDraftRepository.save(entity);
    }
}
