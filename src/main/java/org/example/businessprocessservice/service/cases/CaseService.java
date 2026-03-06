package org.example.businessprocessservice.service.cases;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.entity.CasePartyEntity;
import org.example.businessprocessservice.domain.entity.ProcedureTypeEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.exception.ForbiddenStatusTransitionException;
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CasePartyRepository;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.repository.DocumentRepository;
import org.example.businessprocessservice.repository.ProcedureTypeRepository;
import org.example.businessprocessservice.web.dto.CasePartyShortResponse;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.CreateCaseRequest;
import org.example.businessprocessservice.web.dto.UpdateCaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final CasePartyRepository casePartyRepository;
    private final ProcedureTypeRepository procedureTypeRepository;
    private final DocumentRepository documentRepository;

    public CaseService(CaseRepository caseRepository,
                       CasePartyRepository casePartyRepository,
                       ProcedureTypeRepository procedureTypeRepository,
                       DocumentRepository documentRepository) {
        this.caseRepository = caseRepository;
        this.casePartyRepository = casePartyRepository;
        this.procedureTypeRepository = procedureTypeRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public CaseResponse createCase(CreateCaseRequest req) {
        if (caseRepository.existsByCaseNumber(req.getCaseNumber())) {
            throw new IllegalArgumentException("Case with this number already exists");
        }

        String procedureCode = normalizeCode(req.getProcedureType());
        if (procedureCode.isBlank()) throw new IllegalArgumentException("procedureType is required");

        ProcedureTypeEntity pt = procedureTypeRepository.findByCode(procedureCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown procedureType: " + procedureCode));

        CaseEntity e = new CaseEntity();
        e.setCaseNumber(req.getCaseNumber());
        e.setProcedureType(pt);
        e.setStatus(CaseStatus.CREATED);
        e.setStartDate(LocalDateTime.now());

        return toResponse(caseRepository.save(e));
    }

    @Transactional
    public CaseResponse getCase(Long id) {
        CaseEntity e = caseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Case not found: " + id));
        return toResponse(e);
    }

    // ✅ LIST (поиск + фильтр + пагинация)
    @Transactional
    public Page<CaseResponse> listCases(String caseNumber, CaseStatus status, Pageable pageable) {

        String q = caseNumber == null ? "" : caseNumber.trim();

        Page<CaseEntity> page;
        if (status != null && !q.isBlank()) {
            page = caseRepository.findAllByStatusAndCaseNumberContainingIgnoreCase(status, q, pageable);
        } else if (status != null) {
            page = caseRepository.findAllByStatus(status, pageable);
        } else if (!q.isBlank()) {
            page = caseRepository.findAllByCaseNumberContainingIgnoreCase(q, pageable);
        } else {
            page = caseRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    // ✅ UPDATE Case (только ранние статусы)
    @Transactional
    public CaseResponse updateCase(Long id, UpdateCaseRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is required");

        CaseEntity c = caseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Case not found: " + id));

        // Разрешаем менять атрибуты только в CREATED / IN_PROGRESS
        if (c.getStatus() != CaseStatus.CREATED && c.getStatus() != CaseStatus.IN_PROGRESS) {
            throw new ForbiddenStatusTransitionException(
                    "Cannot update case in status: " + c.getStatus()
            );
        }

        boolean changed = false;

        // caseNumber
        if (req.getCaseNumber() != null) {
            String newNumber = req.getCaseNumber().trim();
            if (newNumber.isBlank()) throw new IllegalArgumentException("caseNumber must not be blank");

            if (!newNumber.equals(c.getCaseNumber()) && caseRepository.existsByCaseNumber(newNumber)) {
                throw new IllegalArgumentException("Case with this number already exists");
            }

            c.setCaseNumber(newNumber);
            changed = true;
        }

        // procedureType
        if (req.getProcedureType() != null) {
            String code = normalizeCode(req.getProcedureType());
            if (code.isBlank()) throw new IllegalArgumentException("procedureType must not be blank");

            ProcedureTypeEntity pt = procedureTypeRepository.findByCode(code)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown procedureType: " + code));

            c.setProcedureType(pt);
            changed = true;
        }

        if (!changed) {
            throw new IllegalArgumentException("Nothing to update");
        }

        return toResponse(caseRepository.save(c));
    }

    // ✅ DELETE Case (только CREATED + нет parties + нет docs)
    @Transactional
    public void deleteCase(Long id) {
        CaseEntity c = caseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Case not found: " + id));

        if (c.getStatus() != CaseStatus.CREATED) {
            throw new ForbiddenStatusTransitionException(
                    "Cannot delete case in status: " + c.getStatus()
            );
        }

        if (casePartyRepository.existsByCaseId(id)) {
            throw new ForbiddenStatusTransitionException(
                    "Cannot delete case " + id + ": it has parties"
            );
        }

        if (documentRepository.existsByCaseId(id)) {
            throw new ForbiddenStatusTransitionException(
                    "Cannot delete case " + id + ": it has documents"
            );
        }

        caseRepository.delete(c);
    }

    private CaseResponse toResponse(CaseEntity e) {
        CaseResponse r = new CaseResponse();
        r.setId(e.getId());
        r.setCaseNumber(e.getCaseNumber());
        r.setProcedureType(e.getProcedureType() == null ? null : e.getProcedureType().getCode());
        r.setStatus(e.getStatus());
        r.setStartDate(e.getStartDate());
        r.setEndDate(e.getEndDate());

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
        if (e.getRole() != null) r.setRoleCode(e.getRole().getCode());
        return r;
    }

    private static String normalizeCode(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }
}