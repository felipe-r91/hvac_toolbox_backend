package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiCustomerReportResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CfrAiReportService {

    private final CfrDraftRepository cfrDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final CfrPromptBuilderService promptBuilderService;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public CfrAiReportService(
            CfrDraftRepository cfrDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            CfrPromptBuilderService promptBuilderService,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.cfrDraftRepository = cfrDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiCustomerReportResponse generate(String cfrId) {
        CfrDraftEntity draft = cfrDraftRepository.findById(cfrId)
                .orElseThrow(() -> new ResourceNotFoundException("CFR draft not found: " + cfrId));

        List<PhotoRecordEntity> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.CFR_DRAFT, cfrId);

        String prompt = promptBuilderService.buildPrompt(draft, photos);

        return openAiReportGenerationService.generateCustomerReport(prompt);
    }
}