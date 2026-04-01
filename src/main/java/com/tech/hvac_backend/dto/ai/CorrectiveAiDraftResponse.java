package com.tech.hvac_backend.dto.ai;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CorrectiveAiDraftResponse {

    private String title;
    private String executiveSummary;
    private String conditionFound;
    private String observations;
    private String diagnosis;
    private String correctiveActions;
    private String recommendations;
    private String returnToServiceStatement;

    public CorrectiveAiDraftResponse() {
    }

    public CorrectiveAiDraftResponse(
            String title,
            String executiveSummary,
            String conditionFound,
            String observations,
            String diagnosis,
            String correctiveActions,
            String recommendations,
            String returnToServiceStatement
    ) {
        this.title = title;
        this.executiveSummary = executiveSummary;
        this.conditionFound = conditionFound;
        this.observations = observations;
        this.diagnosis = diagnosis;
        this.correctiveActions = correctiveActions;
        this.recommendations = recommendations;
        this.returnToServiceStatement = returnToServiceStatement;
    }

}