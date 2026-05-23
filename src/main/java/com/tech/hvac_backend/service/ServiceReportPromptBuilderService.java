package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.ServiceReportDraftEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceReportPromptBuilderService {

    private final ManualKnowledgeService manualKnowledgeService;

    public ServiceReportPromptBuilderService(ManualKnowledgeService manualKnowledgeService) {
        this.manualKnowledgeService = manualKnowledgeService;
    }

    public String buildPrompt(
            ServiceReportDraftEntity draft,
            MachineEntity machine,
            VesselEntity vessel,
            List<PhotoRecordEntity> photos
    ) {
        String photoList = photos == null || photos.isEmpty()
                ? "No attached photos."
                : photos.stream()
                .map(photo -> "- Caption: " + nullSafe(photo.getCaption())
                        + " | Filename: " + nullSafe(photo.getFilename()))
                .collect(Collectors.joining("\n"));

        List<ManualKnowledgeChunkEntity> relevantChunks =
                manualKnowledgeService.findRelevantChunks(draft);

        String manualContext =
                manualKnowledgeService.buildManualContext(relevantChunks);

        return """
                You are generating a customer-ready Johnson Controls Marine & Navy Service Report.

                You are acting as an experienced HVAC/refrigeration field service engineer reviewing service work notes, machine information, photos, and OEM manual references.

                Use the technician notes as the primary field evidence.
                Use the manual reference context only to support the service work performed, maintenance recommendations, and any required follow-up.

                You may form an engineering judgment when supported by the available data.
                You may connect the service work performed to refrigeration cycle behavior, oil system behavior, control logic, sensors, valves, compressor operation, electrical systems, or mechanical condition when the technician notes support it.

                Write in a professional technical field-service tone suitable for a vessel customer.

                Do not fabricate measurements, parts, inspections, test results, or events that are not supported by the provided source data.
                Do not introduce failure analysis, root cause, fault classification, symptoms, alarms, or diagnosis unless those facts are explicitly present in the service work notes.
                When uncertain, use engineering language such as "likely", "possible", or "consistent with".
                Do not copy manual text directly; synthesize it into clear engineering explanations.

                You must return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the response in ```json.
                Do not include explanations.

                The JSON must match EXACTLY this structure:

                {
                  "reportNo": "",
                  "title": "Service Report",
                  "subtitle": "",
                  "company": "Johnson Controls",
                  "branch": "",
                  "date": "",
                  "serviceOrder": "",
                  "engineer": "",
                  "projectManager": "",
                  "location": "",
                  "machineStatus": "",
                  "serviceResult": "",
                  "executiveSummary": "",
                  "conditionFound": "",
                  "alarms": [
                    {
                      "description": "",
                      "status": ""
                    }
                  ],
                  "workConducted": [],
                  "recommendations": [],
                  "furtherActionRequired": "",
                  "ehsStatement": ""
                }

                Field rules:
                - title must be "Service Report".
                - company must be "Johnson Controls".
                - reportNo, branch, serviceOrder, engineer, projectManager, and ehsStatement are layout fields. Return empty string unless explicitly provided.
                - subtitle should summarize the machine, for example: "AC#1 – Liquid Chiller".
                - date should be based on the report created date.
                - location should use the machine location or vessel/location information available.

                - machineStatus should describe the final machine status after the service.
                - If machineReturnedToService is "yes", machineStatus should indicate that the machine was returned to service, unless the source data shows remaining restrictions.
                - If machineReturnedToService is "no", machineStatus should indicate that the machine was not returned to service or remains unavailable.
                - If machineReturnedToService is "unknown", machineStatus should avoid claiming that the machine was returned to service.

                - serviceResult should clearly state whether the machine was returned to service, not returned to service, or if the result is unknown.
                - serviceResult may include a short explanation of remaining restrictions or follow-up when supported by the source data.

                - executiveSummary should summarize:
                  service work performed + final service result + remaining follow-up.

                - conditionFound should remain empty unless the service work notes explicitly describe a condition found during the service.

                - alarms must be an empty array unless alarms or abnormal readings are explicitly mentioned in the service work notes.
                - Do not invent alarm codes, abnormal readings, failure causes, or diagnostic classifications.

                - workConducted must be an array of concise action items.
                - workConducted should be based mainly on workPerformed and machineReturnedToService.
                - You may split the technician's workPerformed text into clear bullet-style actions.
                - Do not add unrelated repair scope or work that was not performed.

                - recommendations must be an array of clear actionable items.
                - Improve wording and split recommendations when useful.
                - Recommendations may use manual context to guide inspection, monitoring, testing, adjustment, or follow-up, but must remain relevant to the reported condition.

                - furtherActionRequired should summarize required follow-up, parts, monitoring, return visit, operational limitation, or pending confirmation based only on the source data.

                Manual reference context:
                %s

                Source report data:

                Report Type:
                Service Report

                Created At:
                %s

                Vessel:
                Name: %s
                IMO Number: %s
                Type: %s
                Owner / Customer: %s
                Contact: %s

                Machine:
                Tag: %s
                Model: %s
                Manufacturer: %s
                Type: %s
                Compressor Type: %s
                Starter Type: %s
                Serial Number: %s
                Location: %s
                Refrigerant: %s
                Oil Type: %s
                Control System: %s
                Software Version: %s

                Work Performed:
                %s

                Recommendations:
                %s

                Further Action Required:
                %s

                Machine Returned To Service:
                %s

                Attached Photos:
                %s
                """.formatted(
                manualContext,
                nullSafe(draft.getCreatedAt()),
                nullSafe(draft.getVesselName()),
                nullSafe(resolveVesselImo(draft.getVesselImo(), vessel)),
                nullSafe(prefer(draft.getVesselType(), vessel != null ? vessel.getVesselType() : null)),
                nullSafe(prefer(draft.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null)),
                nullSafe(prefer(draft.getVesselContact(), vessel != null ? vessel.getVesselContact() : null)),
                nullSafe(draft.getMachineTag()),
                nullSafe(draft.getMachineModel()),
                nullSafe(prefer(draft.getMachineMfg(), machine != null ? machine.getMfg() : null)),
                nullSafe(draft.getMachineType()),
                nullSafe(prefer(draft.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null)),
                nullSafe(draft.getMachineStarterType()),
                nullSafe(prefer(draft.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null)),
                nullSafe(draft.getMachineLocation()),
                nullSafe(prefer(draft.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null)),
                nullSafe(prefer(draft.getMachineOilType(), machine != null ? machine.getOilType() : null)),
                nullSafe(prefer(draft.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null)),
                nullSafe(prefer(draft.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null)),
                nullSafe(draft.getWorkPerformed()),
                nullSafe(draft.getRecommendations()),
                nullSafe(draft.getFurtherActionRequired()),
                nullSafe(draft.getMachineReturnedToService()),
                photoList
        );
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }

    private String prefer(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }

    private String resolveVesselImo(String vesselImo, VesselEntity vessel) {
        return prefer(vesselImo, vessel != null ? vessel.getImoNumber() : null);
    }
}
