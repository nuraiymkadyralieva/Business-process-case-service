package org.example.businessprocessservice.service.cases;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.entity.CasePartyEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.repository.CasePartyRepository;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.web.dto.CasePartyShortResponse;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.CreateCaseRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final CasePartyRepository casePartyRepository;

    public CaseService(CaseRepository caseRepository, CasePartyRepository casePartyRepository) {
        this.caseRepository = caseRepository;
        this.casePartyRepository = casePartyRepository;
    }

    @Transactional
    public CaseResponse createCase(CreateCaseRequest req) {
        if (caseRepository.existsByCaseNumber(req.getCaseNumber())) {
            throw new IllegalArgumentException("Case with this number already exists");
        }

        CaseEntity e = new CaseEntity();
        e.setCaseNumber(req.getCaseNumber());
        e.setProcedureType(req.getProcedureType());
        e.setStatus(CaseStatus.CREATED);
        e.setStartDate(LocalDateTime.now());

        CaseEntity saved = caseRepository.save(e);
        return toResponse(saved);
    }

    @Transactional // ✅ важно, чтобы role.getCode() (LAZY) не падал
    public CaseResponse getCase(Long id) {
        CaseEntity e = caseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));
        return toResponse(e);
    }

    private CaseResponse toResponse(CaseEntity e) {
        CaseResponse r = new CaseResponse();
        r.setId(e.getId());
        r.setCaseNumber(e.getCaseNumber());
        r.setProcedureType(e.getProcedureType());
        r.setStatus(e.getStatus());
        r.setStartDate(e.getStartDate());
        r.setEndDate(e.getEndDate());

        // ✅ участники (кратко: roleCode + partyType + displayName)
        List<CasePartyShortResponse> parties = casePartyRepository
                .findAllByCaseIdOrderByCreatedAtAsc(e.getId())
                .stream()
                .map(CaseService::toShortParty)
                .toList();

        r.setParties(parties);

        return r;
    }

    private static CasePartyShortResponse toShortParty(CasePartyEntity e) {
        CasePartyShortResponse r = new CasePartyShortResponse();
        r.setPartyType(e.getPartyType());
        r.setDisplayName(e.getDisplayName());

        if (e.getRole() != null) {
            r.setRoleCode(e.getRole().getCode());
        }

        return r;
    }
}