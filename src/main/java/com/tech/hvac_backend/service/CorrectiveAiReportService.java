package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiCustomerReportResponse;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorrectiveAiReportService {

    private final CorrectiveDraftRepository correctiveDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final CorrectivePromptBuilderService promptBuilderService;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public CorrectiveAiReportService(
            CorrectiveDraftRepository correctiveDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            CorrectivePromptBuilderService promptBuilderService,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.correctiveDraftRepository = correctiveDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiCustomerReportResponse generate(String draftId) {
        CorrectiveDraftEntity draft = correctiveDraftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Corrective draft not found: " + draftId));

        List<PhotoRecordEntity> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.CORRECTIVE_DRAFT, draftId);

        String prompt = promptBuilderService.buildPrompt(draft, photos);

        return openAiReportGenerationService.generateCustomerReport(prompt);
    }
}