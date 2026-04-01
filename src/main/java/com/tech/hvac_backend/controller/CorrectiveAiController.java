package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.ai.CorrectiveAiDraftResponse;
import com.tech.hvac_backend.service.CorrectiveAiDraftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/corrective-reports")
public class CorrectiveAiController {

    private final CorrectiveAiDraftService correctiveAiDraftService;

    public CorrectiveAiController(CorrectiveAiDraftService correctiveAiDraftService) {
        this.correctiveAiDraftService = correctiveAiDraftService;
    }

    @PostMapping("/{draftId}/generate-ai-draft")
    public ResponseEntity<CorrectiveAiDraftResponse> generateAiDraft(@PathVariable String draftId) {
        return ResponseEntity.ok(correctiveAiDraftService.generate(draftId));
    }
}