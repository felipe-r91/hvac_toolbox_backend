package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiServiceReportResponse;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorrectiveAiReportService {

    private final CorrectiveDraftRepository correctiveDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final CorrectiveServiceReportPromptBuilderService promptBuilderService;
    private final MachineRepository machineRepository;
    private final VesselRepository vesselRepository;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public CorrectiveAiReportService(
            CorrectiveDraftRepository correctiveDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            CorrectiveServiceReportPromptBuilderService promptBuilderService,
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.correctiveDraftRepository = correctiveDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiServiceReportResponse generate(String correctiveId) {
        CorrectiveDraftEntity draft = correctiveDraftRepository.findById(correctiveId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Corrective draft not found: " + correctiveId
                ));

        List<PhotoRecordEntity> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
                        PhotoOwnerType.CORRECTIVE_DRAFT,
                        correctiveId
                );

        MachineEntity machine = machineRepository.findById(draft.getMachineId())
                .orElse(null);
        VesselEntity vessel = vesselRepository.findById(draft.getVesselId())
                .orElse(null);

        String prompt = promptBuilderService.buildPrompt(draft, machine, vessel, photos);

        return openAiReportGenerationService.generateServiceReport(prompt);
    }
}
