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
        entity.setMachineId(request.getMachineId());
        entity.setMachineTag(request.getMachineTag());
        entity.setMachineModel(request.getMachineModel());
        entity.setMachineType(request.getMachineType());
        entity.setMachineStarterType(request.getMachineStarterType());
        entity.setMachineLocation(request.getMachineLocation());
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