package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cfr")
@CrossOrigin
public class CfrQueryController {

    private final CfrDraftRepository cfrDraftRepository;

    public CfrQueryController(CfrDraftRepository cfrDraftRepository) {
        this.cfrDraftRepository = cfrDraftRepository;
    }

    @GetMapping
    public List<CfrDraftEntity> getAll() {
        return cfrDraftRepository.findAllByOrderByCreatedAtDesc();
    }
}