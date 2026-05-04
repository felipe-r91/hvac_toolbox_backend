package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiCustomerReportResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CfrAiReportService {

    private final CfrDraftRepository cfrDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final CfrPromptBuilderService promptBuilderService;
    private final MachineRepository machineRepository;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public CfrAiReportService(
            CfrDraftRepository cfrDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            CfrPromptBuilderService promptBuilderService, MachineRepository machineRepository,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.cfrDraftRepository = cfrDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.machineRepository = machineRepository;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiCustomerReportResponse generate(String cfrId) {
        CfrDraftEntity draft = cfrDraftRepository.findById(cfrId)
                .orElseThrow(() -> new ResourceNotFoundException("CFR draft not found: " + cfrId));

        List<PhotoRecordEntity> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.CFR_DRAFT, cfrId);

        MachineEntity machine = machineRepository.findById(draft.getMachineId()).orElse(null);

        String prompt = promptBuilderService.buildPrompt(draft, machine, photos);

        return openAiReportGenerationService.generateCustomerReport(prompt);
    }
}