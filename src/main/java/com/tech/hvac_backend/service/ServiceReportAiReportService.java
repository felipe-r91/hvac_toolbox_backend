package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiServiceReportResponse;
import com.tech.hvac_backend.entity.ServiceReportDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.ServiceReportDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceReportAiReportService {

    private final ServiceReportDraftRepository serviceReportDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final ServiceReportPromptBuilderService promptBuilderService;
    private final MachineRepository machineRepository;
    private final VesselRepository vesselRepository;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public ServiceReportAiReportService(
            ServiceReportDraftRepository serviceReportDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            ServiceReportPromptBuilderService promptBuilderService,
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.serviceReportDraftRepository = serviceReportDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiServiceReportResponse generate(String serviceReportId) {
        ServiceReportDraftEntity draft = serviceReportDraftRepository.findById(serviceReportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service report draft not found: " + serviceReportId
                ));

        List<PhotoRecordEntity> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
                        PhotoOwnerType.SERVICE_REPORT_DRAFT,
                        serviceReportId
                );

        MachineEntity machine = machineRepository.findById(draft.getMachineId())
                .orElse(null);
        VesselEntity vessel = vesselRepository.findById(draft.getVesselId())
                .orElse(null);

        String prompt = promptBuilderService.buildPrompt(draft, machine, vessel, photos);

        return openAiReportGenerationService.generateServiceReport(prompt);
    }
}
