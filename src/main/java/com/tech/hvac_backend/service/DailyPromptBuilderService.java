package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyPromptBuilderService {

    private final ManualKnowledgeService manualKnowledgeService;

    public DailyPromptBuilderService(ManualKnowledgeService manualKnowledgeService) {
        this.manualKnowledgeService = manualKnowledgeService;
    }

    public String buildPrompt(
            DailyDraftEntity draft,
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
                You are generating a customer-ready Johnson Controls Marine & Navy Daily Report.

                You are acting as an experienced HVAC/refrigeration field service engineer reviewing a daily technician update, alarm status, work completed today, follow-up actions, machine information, photos, and OEM manual references.

                Use the technician's daily notes as the primary field evidence.
                Use the manual reference context only to support technical clarity, risk explanation, and relevant follow-up recommendations.

                Write in a professional technical field-service tone suitable for a vessel customer.

                Do not fabricate measurements, inspections, parts, alarms, test results, completed work, or events that are not supported by the provided source data.
                Do not state that an alarm was cleared unless the source data explicitly supports it.
                When uncertain, use engineering language such as "reported", "appears", "should be verified", or "requires follow-up".
                Do not copy manual text directly; synthesize it into clear engineering explanations.

                You must return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the response in ```json.
                Do not include explanations.

                The JSON must match EXACTLY this structure:

                {
                  "reportNo": "",
                  "title": "Daily Report",
                  "subtitle": "",
                  "company": "Johnson Controls",
                  "branch": "",
                  "date": "",
                  "serviceOrder": "",
                  "engineer": "",
                  "projectManager": "",
                  "location": "",
                  "machineStatus": "",
                  "alarmStatus": "",
                  "executiveSummary": "",
                  "dailySummary": "",
                  "failureNotes": "",
                  "alarms": [
                    {
                      "description": "",
                      "status": ""
                    }
                  ],
                  "workConductedToday": [],
                  "recommendations": [],
                  "furtherActions": "",
                  "ehsStatement": ""
                }

                Field rules:
                - title must be "Daily Report".
                - company must be "Johnson Controls".
                - reportNo, branch, serviceOrder, engineer, projectManager, and ehsStatement are layout fields. Return empty string unless explicitly provided.
                - subtitle should summarize the machine, for example: "AC#1 - Liquid Chiller".
                - date should be based on the report created date.
                - location should use the machine location or vessel/location information available.

                - machineStatus must be evaluated from the source data and must be exactly one of these two strings:
                  "Work Finished" or "Work In Progress".
                - Use "Work Finished" only when the daily notes clearly indicate today's work is complete and no pending follow-up, monitoring, unresolved alarm, return visit, parts, or further action remains.
                - Use "Work In Progress" when alarmPresent is true, furtherActions contains any pending item, the notes mention follow-up/monitoring/parts/return visit, or the source data does not clearly confirm the work is finished.
                - Do not return any other machineStatus value. Do not add extra words, punctuation, or explanation inside machineStatus.
                - alarmStatus should be "Alarm reported" when alarmPresent is true, otherwise "No alarm reported", unless source notes require a more specific neutral wording.

                - executiveSummary should summarize:
                  today's status + alarm condition + work conducted + follow-up required.

                - dailySummary should be a concise customer-ready narrative of the daily update.
                - failureNotes should professionally rewrite the failure/alarm notes without changing their meaning. If no failure notes were provided, return an empty string.

                - alarms must be an array of objects with description and status.
                - If alarmPresent is false and no alarm or failure notes are provided, return an empty alarms array.
                - If alarmPresent is true but details are limited, include one alarm item using the available failure notes or a neutral "Alarm reported by technician" description.
                - For alarm status, use "Open", "Monitoring", or empty string unless the source data clearly indicates a different status.

                - workConductedToday must be an array of concise action items based only on workConductedToday.
                - recommendations must be clear, actionable, and relevant to the daily status. Use manual context only when it directly supports the reported condition or follow-up.
                - furtherActions should summarize required follow-up, monitoring, return visit, parts, pending confirmation, or operational limitation based only on source data.

                Manual reference context:
                %s

                Source report data:

                Report Type:
                Daily Report

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

                Alarm Present:
                %s

                Failure Classification:
                Component: %s
                Mode: %s
                Code: %s

                Failure Notes:
                %s

                Work Conducted Today:
                %s

                Further Actions:
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
                draft.isAlarmPresent() ? "Yes" : "No",
                nullSafe(draft.getFailureComponent()),
                nullSafe(draft.getFailureMode()),
                nullSafe(draft.getFailureCode()),
                nullSafe(draft.getFailureNotes()),
                nullSafe(draft.getWorkConductedToday()),
                nullSafe(draft.getFurtherActions()),
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
