package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiDailyReportResponse;
import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyAiReportService {

    private final DailyDraftRepository dailyDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final DailyPromptBuilderService promptBuilderService;
    private final MachineRepository machineRepository;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public DailyAiReportService(
            DailyDraftRepository dailyDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            DailyPromptBuilderService promptBuilderService,
            MachineRepository machineRepository,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.dailyDraftRepository = dailyDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.machineRepository = machineRepository;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiDailyReportResponse generate(String dailyId) {
        DailyDraftEntity draft = dailyDraftRepository.findById(dailyId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily draft not found: " + dailyId));

        List<PhotoRecordEntity> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.DAILY_DRAFT, dailyId);

        MachineEntity machine = machineRepository.findById(draft.getMachineId()).orElse(null);

        String prompt = promptBuilderService.buildPrompt(draft, machine, photos);

        return openAiReportGenerationService.generateDailyReport(prompt);
    }
}
