package org.example.businessprocessservice.web.controllers;

import org.example.businessprocessservice.domain.entity.StatusHistoryEntity;
import org.example.businessprocessservice.repository.StatusHistoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseHistoryController {

    private final StatusHistoryRepository historyRepository;

    public CaseHistoryController(StatusHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @GetMapping("/{id}/history")
    public List<StatusHistoryEntity> history(@PathVariable Long id) {
        return historyRepository.findByCaseIdOrderByChangedAtAsc(id);
    }
}