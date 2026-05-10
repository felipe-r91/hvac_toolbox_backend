package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/daily")
@CrossOrigin
public class DailyQueryController {

    private final DailyDraftRepository dailyDraftRepository;

    public DailyQueryController(DailyDraftRepository dailyDraftRepository) {
        this.dailyDraftRepository = dailyDraftRepository;
    }

    @GetMapping
    public List<DailyDraftEntity> getAll() {
        return dailyDraftRepository.findAllByOrderByCreatedAtDesc();
    }
}
