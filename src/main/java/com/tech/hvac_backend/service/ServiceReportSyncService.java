package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.sync.ServiceReportSyncRequest;
import com.tech.hvac_backend.entity.ServiceReportDraftEntity;
import com.tech.hvac_backend.repository.ServiceReportDraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceReportSyncService {

    private final ServiceReportDraftRepository serviceReportDraftRepository;

    public ServiceReportSyncService(ServiceReportDraftRepository serviceReportDraftRepository) {
        this.serviceReportDraftRepository = serviceReportDraftRepository;
    }

    @Transactional
    public boolean syncServiceReportDraft(ServiceReportSyncRequest request) {
        validateServiceReportRequest(request);

        if (serviceReportDraftRepository.existsById(request.getId())) {
            return false;
        }

        ServiceReportDraftEntity draftEntity = mapServiceReportDraft(request);
        serviceReportDraftRepository.save(draftEntity);

        return true;
    }

    private void validateServiceReportRequest(ServiceReportSyncRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        if (isBlank(request.getId())) {
            throw new IllegalArgumentException("Service report id is required.");
        }

        if (isBlank(request.getVesselId())) {
            throw new IllegalArgumentException("Vessel id is required.");
        }

        if (isBlank(request.getMachineId())) {
            throw new IllegalArgumentException("Machine id is required.");
        }

        if (isBlank(request.getCreatedAt())) {
            throw new IllegalArgumentException("CreatedAt is required.");
        }
    }

    private ServiceReportDraftEntity mapServiceReportDraft(ServiceReportSyncRequest request) {
        ServiceReportDraftEntity entity = new ServiceReportDraftEntity();
        entity.setId(request.getId());
        entity.setVesselId(request.getVesselId());
        entity.setVesselName(request.getVesselName());
        entity.setVesselImo(request.getVesselImo());
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

        entity.setWorkPerformed(request.getWorkPerformed());
        entity.setRecommendations(request.getRecommendations());
        entity.setFurtherActionRequired(request.getFurtherActionRequired());
        entity.setSourcePreventiveReportId(request.getSourcePreventiveReportId());

        entity.setMachineReturnedToService(
                isBlank(request.getMachineReturnedToService())
                        ? "unknown"
                        : request.getMachineReturnedToService()
        );

        entity.setReportCategory(
                isBlank(request.getReportCategory()) ? "service_report" : request.getReportCategory()
        );

        entity.setSynced(Boolean.TRUE);

        return entity;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
