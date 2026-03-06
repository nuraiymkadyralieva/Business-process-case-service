package org.example.businessprocessservice.service.cases;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.entity.CasePartyEntity;
import org.example.businessprocessservice.domain.entity.ProcedureTypeEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.repository.CasePartyRepository;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.repository.ProcedureTypeRepository;
import org.example.businessprocessservice.web.dto.CasePartyShortResponse;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.CreateCaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final CasePartyRepository casePartyRepository;
    private final ProcedureTypeRepository procedureTypeRepository;

    public CaseService(
            CaseRepository caseRepository,
            CasePartyRepository casePartyRepository,
            ProcedureTypeRepository procedureTypeRepository
    ) {
        this.caseRepository = caseRepository;
        this.casePartyRepository = casePartyRepository;
        this.procedureTypeRepository = procedureTypeRepository;
    }

    @Transactional
    public CaseResponse createCase(CreateCaseRequest req) {
        if (caseRepository.existsByCaseNumber(req.getCaseNumber())) {
            throw new IllegalArgumentException("Case with this number already exists");
        }

        String procedureCode = normalizeCode(req.getProcedureType());
        if (procedureCode.isBlank()) {
            throw new IllegalArgumentException("procedureType is required");
        }

        ProcedureTypeEntity pt = procedureTypeRepository.findByCode(procedureCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown procedureType: " + procedureCode));

        CaseEntity e = new CaseEntity();
        e.setCaseNumber(req.getCaseNumber());
        e.setProcedureType(pt); // ✅ теперь это справочник (entity), а не строка
        e.setStatus(CaseStatus.CREATED);
        e.setStartDate(LocalDateTime.now());

        CaseEntity saved = caseRepository.save(e);
        return toResponse(saved);
    }

    @Transactional // ✅ важно, чтобы LAZY (role/procedureType) спокойно читались
    public CaseResponse getCase(Long id) {
        CaseEntity e = caseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));
        return toResponse(e);
    }

    private CaseResponse toResponse(CaseEntity e) {
        CaseResponse r = new CaseResponse();
        r.setId(e.getId());
        r.setCaseNumber(e.getCaseNumber());

        // ✅ в API отдаём КОД типа процедуры
        r.setProcedureType(e.getProcedureType() == null ? null : e.getProcedureType().getCode());

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

    private static String normalizeCode(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }
    @Transactional
    public Page<CaseResponse> listCases(String caseNumber, CaseStatus status, Pageable pageable) {

        String q = caseNumber == null ? "" : caseNumber.trim();

        Page<CaseEntity> page;

        if (status != null && !q.isBlank()) {
            page = caseRepository.findAllByStatusAndCaseNumberContainingIgnoreCase(status, q, pageable);
        } else if (status != null) {
            page = caseRepository.findAllByStatus(status, pageable); // см. ниже
        } else if (!q.isBlank()) {
            page = caseRepository.findAllByCaseNumberContainingIgnoreCase(q, pageable);
        } else {
            page = caseRepository.findAll(pageable);
        }

        return page.map(this::toResponse);}
    }