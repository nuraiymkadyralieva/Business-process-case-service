package org.example.businessprocessservice.web.controllers;

import jakarta.validation.Valid;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.service.cases.CaseService;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.CreateCaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

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

    // ✅ ВОТ ЭТОГО НЕ ХВАТАЛО: список + фильтрация + поиск + пагинация
    @GetMapping
    public Page<CaseResponse> list(
            @RequestParam(required = false) String caseNumber,
            @RequestParam(required = false) CaseStatus status,
            @PageableDefault(size = 10)
            @SortDefault(sort = "startDate", direction = DESC)
            Pageable pageable
    ) {
        return caseService.listCases(caseNumber, status, pageable);
    }
}