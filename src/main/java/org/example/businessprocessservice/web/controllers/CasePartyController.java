package org.example.businessprocessservice.web.controllers;

import org.example.businessprocessservice.service.cases.CasePartyService;
import org.example.businessprocessservice.web.dto.CasePartyResponse;
import org.example.businessprocessservice.web.dto.CreateCasePartyRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/parties")
public class CasePartyController {

    private final CasePartyService service;

    public CasePartyController(CasePartyService service) {
        this.service = service;
    }

    @PostMapping
    public CasePartyResponse add(@PathVariable Long caseId, @RequestBody CreateCasePartyRequest req) {
        return service.addParty(caseId, req);
    }

    @GetMapping
    public List<CasePartyResponse> list(@PathVariable Long caseId) {
        return service.listParties(caseId);
    }

    @DeleteMapping("/{partyId}")
    public void delete(@PathVariable Long caseId, @PathVariable Long partyId) {
        service.deleteParty(caseId, partyId);
    }
}