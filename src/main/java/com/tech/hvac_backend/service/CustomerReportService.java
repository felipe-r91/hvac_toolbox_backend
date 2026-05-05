package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.customerReport.CreateCustomerReportRequest;
import com.tech.hvac_backend.dto.customerReport.CustomerReportResponse;
import com.tech.hvac_backend.entity.CustomerReportEntity;
import com.tech.hvac_backend.repository.CustomerReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerReportService {

    private final CustomerReportRepository customerReportRepository;
    private final R2DocumentStorageService documentStorageService;

    public CustomerReportService(
            CustomerReportRepository customerReportRepository,
            R2DocumentStorageService documentStorageService
    ) {
        this.customerReportRepository = customerReportRepository;
        this.documentStorageService = documentStorageService;
    }

    public CustomerReportResponse createCustomerReport(
            CreateCustomerReportRequest request,
            MultipartFile file
    ) {
        return saveCustomerReport(request, file);
    }

    public CustomerReportResponse saveCustomerReport(
            CreateCustomerReportRequest request,
            MultipartFile pdfFile
    ) {
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required.");
        }

        String pdfFilename = buildPdfFilename(request);
        String objectKey = buildObjectKey(request, pdfFilename);

        documentStorageService.uploadPdf(objectKey, pdfFile);

        CustomerReportEntity entity = new CustomerReportEntity();

        entity.setSourceReportId(request.getSourceReportId());
        entity.setSourceReportType(request.getSourceReportType());

        entity.setVesselId(request.getVesselId());
        entity.setVesselName(request.getVesselName());

        entity.setMachineId(request.getMachineId());
        entity.setMachineTag(request.getMachineTag());
        entity.setMachineModel(request.getMachineModel());
        entity.setMachineType(request.getMachineType());
        entity.setMachineStatus(request.getMachineStatus());

        entity.setTitle(request.getTitle());
        entity.setReportDate(LocalDateTime.now());

        entity.setPdfObjectKey(objectKey);
        entity.setPdfFilename(pdfFilename);

        entity.setCreatedBy(request.getCreatedBy());
        entity.setCreatedAt(LocalDateTime.now());

        return toResponse(customerReportRepository.save(entity));
    }

    public List<CustomerReportResponse> findReports(
            String vesselId,
            String machineId,
            String reportType
    ) {
        if (vesselId != null && !vesselId.isBlank()) {
            return findByVesselId(vesselId);
        }

        if (machineId != null && !machineId.isBlank()) {
            return findByMachineId(machineId);
        }

        if (reportType != null && !reportType.isBlank()) {
            return customerReportRepository
                    .findBySourceReportTypeOrderByReportDateDesc(reportType)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return findAll();
    }

    public CustomerReportResponse findById(UUID reportId) {
        return customerReportRepository.findById(reportId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Customer report not found."));
    }

    public List<CustomerReportResponse> findAll() {
        return customerReportRepository
                .findAllByOrderByReportDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CustomerReportResponse> findByVesselId(String vesselId) {
        return customerReportRepository
                .findByVesselIdOrderByReportDateDesc(vesselId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CustomerReportResponse> findByMachineId(String machineId) {
        return customerReportRepository
                .findByMachineIdOrderByReportDateDesc(machineId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public String generateDownloadUrl(UUID reportId) {
        CustomerReportEntity report = customerReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Customer report not found."));

        return documentStorageService.generatePresignedDownloadUrl(report.getPdfObjectKey());
    }

    private String buildPdfFilename(CreateCustomerReportRequest request) {
        String type = safe(request.getSourceReportType(), "report");
        String machineTag = safe(request.getMachineTag(), "machine");
        String date = LocalDateTime.now().toLocalDate().toString();

        return "%s-%s-%s.pdf"
                .formatted(type, machineTag, date)
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String buildObjectKey(CreateCustomerReportRequest request, String pdfFilename) {
        return "customer-reports/%s/%s/%s"
                .formatted(
                        safe(request.getVesselId(), "unknown-vessel"),
                        safe(request.getMachineId(), "unknown-machine"),
                        pdfFilename
                );
    }

    private CustomerReportResponse toResponse(CustomerReportEntity entity) {
        return CustomerReportResponse.builder()
                .id(entity.getId())
                .sourceReportId(entity.getSourceReportId())
                .sourceReportType(entity.getSourceReportType())
                .vesselId(entity.getVesselId())
                .vesselName(entity.getVesselName())
                .machineId(entity.getMachineId())
                .machineTag(entity.getMachineTag())
                .machineModel(entity.getMachineModel())
                .machineType(entity.getMachineType())
                .machineStatus(entity.getMachineStatus())
                .title(entity.getTitle())
                .reportDate(entity.getReportDate())
                .pdfFilename(entity.getPdfFilename())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}