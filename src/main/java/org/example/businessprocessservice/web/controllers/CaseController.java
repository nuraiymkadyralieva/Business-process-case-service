package org.example.businessprocessservice.web.controllers;

import jakarta.validation.Valid;
import org.example.businessprocessservice.service.cases.CaseService;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.CreateCaseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseResponse create(@Valid @RequestBody CreateCaseRequest req) {
        return caseService.createCase(req);
    }

    @GetMapping("/{id}")
    public CaseResponse get(@PathVariable Long id) {
        return caseService.getCase(id);
    }
}