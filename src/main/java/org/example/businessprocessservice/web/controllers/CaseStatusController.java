package org.example.businessprocessservice.web.controllers;

import jakarta.validation.Valid;
import org.example.businessprocessservice.service.status.CaseStatusService;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.ChangeStatusRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases")
public class CaseStatusController {

    private final CaseStatusService caseStatusService;

    public CaseStatusController(CaseStatusService caseStatusService) {
        this.caseStatusService = caseStatusService;
    }

    @PostMapping("/{id}/status")
    public CaseResponse changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRequest req) {
        return caseStatusService.changeStatus(id, req);
    }
}