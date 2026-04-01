package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

@Service
public class CorrectivePromptBuilderService {

    public String buildPrompt(CorrectiveDraftEntity draft, List<PhotoRecordEntity> photos) {
        StringJoiner photoLines = new StringJoiner("\n");

        for (PhotoRecordEntity photo : photos) {
            photoLines.add("- Photo caption: " + safe(photo.getCaption()) + " | filename: " + safe(photo.getFilename()));
        }

        return """
                You are an expert industrial HVAC and marine service report writer.

                Generate a professional Condition Found Report draft in JSON format.
                Use clear technical language, concise professional tone, and avoid exaggeration.
                Do not invent facts. Only use the provided field data.
                If some field is missing, keep the section brief and neutral.

                Return valid JSON with exactly these keys:
                {
                  "title": string,
                  "executiveSummary": string,
                  "conditionFound": string,
                  "observations": string,
                  "diagnosis": string,
                  "correctiveActions": string,
                  "recommendations": string,
                  "returnToServiceStatement": string
                }

                FIELD DATA
                Vessel: %s
                Machine tag: %s
                Machine model: %s
                Machine type: %s
                Starter type: %s
                Machine location: %s

                Problem summary: %s
                Condition found: %s
                Symptoms observed: %s
                Alarms observed: %s
                Operational impact: %s

                Preliminary diagnosis: %s
                Confirmed cause: %s

                Corrective action: %s
                Recommendations: %s
                Further action required: %s
                Machine returned to service: %s

                Photo notes:
                %s
                """.formatted(
                safe(draft.getVesselName()),
                safe(draft.getMachineTag()),
                safe(draft.getMachineModel()),
                safe(draft.getMachineType()),
                safe(draft.getMachineStarterType()),
                safe(draft.getMachineLocation()),
                safe(draft.getProblemSummary()),
                safe(draft.getConditionFound()),
                safe(draft.getSymptomsObserved()),
                safe(draft.getAlarmsObserved()),
                safe(draft.getOperationalImpact()),
                safe(draft.getPreliminaryDiagnosis()),
                safe(draft.getConfirmedCause()),
                safe(draft.getCorrectiveAction()),
                safe(draft.getRecommendations()),
                safe(draft.getFurtherActionRequired()),
                safe(draft.getMachineReturnedToService()),
                photoLines.length() == 0 ? "- No photo captions provided" : photoLines.toString()
        );
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }
}