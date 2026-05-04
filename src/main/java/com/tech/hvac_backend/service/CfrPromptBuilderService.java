package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CfrPromptBuilderService {

    private final ManualKnowledgeService manualKnowledgeService;

    public CfrPromptBuilderService(ManualKnowledgeService manualKnowledgeService) {
        this.manualKnowledgeService = manualKnowledgeService;
    }

    public String buildPrompt(CfrDraftEntity draft, MachineEntity machine, List<PhotoRecordEntity> photos) {

        String photoList = photos.stream()
                .map(photo -> "- Caption: " + nullSafe(photo.getCaption())
                        + " | Filename: " + nullSafe(photo.getFilename()))
                .collect(Collectors.joining("\n"));

        // 🔹 Get manual knowledge
        List<ManualKnowledgeChunkEntity> relevantChunks =
                manualKnowledgeService.findRelevantChunks(draft);

        String manualContext =
                manualKnowledgeService.buildManualContext(relevantChunks);

        return """
                You are generating a customer-ready Johnson Controls Marine & Navy Conditions Found Report.

                You are acting as an experienced HVAC/refrigeration engineer reviewing technician notes and OEM manual references.

                Use the technician notes as the primary field evidence.
                Use the manual reference context to support engineering reasoning, interpret the condition, explain risks, and guide conclusions.

                You may form an engineering judgment when supported by the available data.
                You may connect symptoms to refrigeration cycle behavior, oil system behavior, control logic, sensors, valves, compressor operation, or electrical systems.

                Write in a professional technical field-service tone suitable for a vessel customer.

                Do not fabricate measurements, alarms, inspections, or completed work.
                Do not state that a component definitively failed unless supported by technician evidence or confirmed cause.
                When uncertain, use engineering language such as "likely", "possible", or "consistent with".
                Do not copy manual text directly; synthesize it into clear engineering explanations.

                You must return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the response in ```json.
                Do not include explanations.

                The JSON must match EXACTLY this structure:

                {
                  "reportNo": "",
                  "title": "Conditions Found Report",
                  "subtitle": "",
                  "company": "Johnson Controls",
                  "branch": "",
                  "date": "",
                  "serviceOrder": "",
                  "engineer": "",
                  "projectManager": "",
                  "location": "",
                  "machineStatus": "",
                  "severity": "",
                  "finalCondition": "",
                  "executiveSummary": "",
                  "conditionFound": "",
                  "alarms": [],
                  "operationalImpact": "",
                  "probableRootCause": "",
                  "recommendations": [],
                  "furtherActionRequired": "",
                  "ehsStatement": ""
                }

                Field rules:
                - reportNo, branch, serviceOrder, engineer, projectManager and ehsStatement are layout fields. Return empty string unless explicitly provided.
                - title must be "Conditions Found Report".
                - company must be "Johnson Controls".
                - subtitle should summarize the machine (e.g. "AC#1 – Liquid Chiller").
                - date should be based on the report created date.
                - location should use the machine location or vessel information.

                - severity should be "Low", "Medium", or "High" based on condition, machine status, alarms, operational impact, and engineering judgment.

                - finalCondition should describe the actual state of the machine (operational, degraded, unsafe, offline, etc).

                - executiveSummary should clearly explain:
                  condition + risk + current status + required action.

                - conditionFound must rewrite technician notes clearly without changing meaning.

                - operationalImpact should explain real system impact (capacity loss, instability, safety concern, efficiency loss, etc).

                - probableRootCause should:
                  use confirmed cause if available,
                  otherwise infer the most likely technical cause using symptoms + engineering reasoning + manual context.

                - If root cause is uncertain, clearly state that and suggest what should be verified.

                - alarms must be an array of strings.

                - recommendations must:
                  be clear, actionable, and technically relevant,
                  may include inspection, adjustment, monitoring, repair, or further diagnostics.

                - furtherActionRequired should summarize next steps, parts, or follow-up visit.

                Manual reference context:
                %s

                Source report data:

                Created At:
                %s

                Vessel:
                Name: %s

                Machine:
                Tag: %s
                Model: %s
                Type: %s
                Starter Type: %s
                Serial Number: %s
                Location: %s

                Machine Status:
                %s

                Failure Classification:
                Component: %s
                Mode: %s
                Code: %s

                Condition Found:
                %s

                Symptoms Observed:
                %s

                Alarms / Abnormal Readings:
                %s

                Operational Impact:
                %s

                Preliminary Diagnosis:
                %s

                Confirmed Cause:
                %s

                Recommendations:
                %s

                Further Action Required:
                %s

                Attached Photos:
                %s
                """.formatted(
                manualContext,
                nullSafe(draft.getCreatedAt()),
                nullSafe(draft.getVesselName()),
                nullSafe(draft.getMachineTag()),
                nullSafe(draft.getMachineModel()),
                nullSafe(draft.getMachineType()),
                nullSafe(draft.getMachineStarterType()),
                nullSafe(machine != null ? machine.getSerialNumber() : null),
                nullSafe(draft.getMachineLocation()),
                nullSafe(draft.getMachineStatus()),
                nullSafe(draft.getFailureComponent()),
                nullSafe(draft.getFailureMode()),
                nullSafe(draft.getFailureCode()),
                nullSafe(draft.getConditionFound()),
                nullSafe(draft.getSymptomsObserved()),
                nullSafe(draft.getAlarmsObserved()),
                nullSafe(draft.getOperationalImpact()),
                nullSafe(draft.getPreliminaryDiagnosis()),
                nullSafe(draft.getConfirmedCause()),
                nullSafe(draft.getRecommendations()),
                nullSafe(draft.getFurtherActionRequired()),
                photoList.isBlank() ? "No attached photos." : photoList
        );
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }
}