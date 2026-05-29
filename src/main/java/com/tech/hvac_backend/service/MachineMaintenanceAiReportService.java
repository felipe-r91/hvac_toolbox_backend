package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiMachineMaintenanceReportResponse;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.PreventiveReportTaskEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import com.tech.hvac_backend.repository.PreventiveReportTaskRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class MachineMaintenanceAiReportService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final PreventiveReportTaskRepository preventiveReportTaskRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final MachineMaintenancePromptBuilderService promptBuilderService;
    private final MachineRepository machineRepository;
    private final VesselRepository vesselRepository;
    private final OpenAiReportGenerationService openAiReportGenerationService;

    public MachineMaintenanceAiReportService(
            PreventiveReportRepository preventiveReportRepository,
            PreventiveReportTaskRepository preventiveReportTaskRepository,
            PhotoRecordRepository photoRecordRepository,
            MachineMaintenancePromptBuilderService promptBuilderService,
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            OpenAiReportGenerationService openAiReportGenerationService
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.preventiveReportTaskRepository = preventiveReportTaskRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.promptBuilderService = promptBuilderService;
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.openAiReportGenerationService = openAiReportGenerationService;
    }

    public AiMachineMaintenanceReportResponse generate(String reportId) {
        PreventiveReportEntity report = preventiveReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Machine maintenance report not found: " + reportId
                ));

        List<PreventiveReportTaskEntity> tasks = preventiveReportTaskRepository
                .findByReportIdOrderByCategoryAscTaskNameAsc(reportId);

        List<PhotoRecordEntity> machinePhotos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
                        PhotoOwnerType.PREVENTIVE_MACHINE,
                        reportId
                );

        List<PhotoRecordEntity> taskPhotos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
                        PhotoOwnerType.PREVENTIVE_TASK,
                        reportId
                );

        List<PhotoRecordEntity> photos = Stream.concat(machinePhotos.stream(), taskPhotos.stream())
                .toList();

        MachineEntity machine = machineRepository.findById(report.getMachineId()).orElse(null);
        VesselEntity vessel = vesselRepository.findById(report.getVesselId()).orElse(null);

        String prompt = promptBuilderService.buildPrompt(report, machine, vessel, tasks, photos);

        return openAiReportGenerationService.generateMachineMaintenanceReport(prompt);
    }
}
