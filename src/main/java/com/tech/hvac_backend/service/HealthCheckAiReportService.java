package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiHealthCheckReportResponse;
import com.tech.hvac_backend.entity.HealthCheckReportEntity;
import com.tech.hvac_backend.entity.HealthCheckReportTaskEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.HealthCheckReportRepository;
import com.tech.hvac_backend.repository.HealthCheckReportTaskRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthCheckAiReportService {

    private final HealthCheckReportRepository healthCheckReportRepository;
    private final HealthCheckReportTaskRepository healthCheckReportTaskRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final HealthCheckPromptBuilderService promptBuilderService;
    private final MachineRepository machineRepository;
    private final VesselRepository vesselRepository;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public HealthCheckAiReportService(
            HealthCheckReportRepository healthCheckReportRepository,
            HealthCheckReportTaskRepository healthCheckReportTaskRepository,
            PhotoRecordRepository photoRecordRepository,
            HealthCheckPromptBuilderService promptBuilderService,
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.healthCheckReportRepository = healthCheckReportRepository;
        this.healthCheckReportTaskRepository = healthCheckReportTaskRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiHealthCheckReportResponse generate(String reportId) {
        HealthCheckReportEntity report = healthCheckReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Health check report not found: " + reportId
                ));

        List<HealthCheckReportTaskEntity> tasks = healthCheckReportTaskRepository
                .findByReportIdOrderByCategoryAscTaskNameAsc(reportId);

        List<PhotoRecordEntity> taskPhotos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
                        PhotoOwnerType.HEALTH_CHECK_TASK,
                        reportId
                );

        MachineEntity machine = machineRepository.findById(report.getMachineId()).orElse(null);
        VesselEntity vessel = vesselRepository.findById(report.getVesselId()).orElse(null);

        String prompt = promptBuilderService.buildPrompt(report, machine, vessel, tasks, taskPhotos);

        return openAiReportGenerationService.generateHealthCheckReport(prompt);
    }
}
