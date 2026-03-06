package org.example.businessprocessservice.service.status;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.entity.CasePartyEntity;
import org.example.businessprocessservice.domain.entity.StatusHistoryEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.exception.ForbiddenStatusTransitionException;
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CasePartyRepository;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.repository.DocumentRepository;
import org.example.businessprocessservice.repository.StatusHistoryRepository;
import org.example.businessprocessservice.web.dto.CasePartyShortResponse;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.ChangeStatusRequest;
import org.example.businessprocessservice.web.dto.ChangeStatusResponse;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CaseStatusService {

    private static final Logger log = LoggerFactory.getLogger(CaseStatusService.class);

    private final CaseRepository caseRepository;
    private final StatusHistoryRepository historyRepository;
    private final CasePartyRepository casePartyRepository;
    private final DocumentRepository documentRepository;

    public CaseStatusService(
            CaseRepository caseRepository,
            StatusHistoryRepository historyRepository,
            CasePartyRepository casePartyRepository,
            DocumentRepository documentRepository
    ) {
        this.caseRepository = caseRepository;
        this.historyRepository = historyRepository;
        this.casePartyRepository = casePartyRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public ChangeStatusResponse changeStatus(Long caseId, ChangeStatusRequest req) {

        if (req == null) throw new IllegalArgumentException("Request body is required");

        CaseEntity c = caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        CaseStatus from = c.getStatus();
        CaseStatus to = req.getNewStatus();

        if (to == null) throw new IllegalArgumentException("newStatus is required");

        if (!TransitionRules.canMove(from, to)) {
            throw new ForbiddenStatusTransitionException("Forbidden transition: " + from + " -> " + to);
        }

        checkPreconditions(c, from, to, req);

        c.setStatus(to);

        if (to == CaseStatus.COMPLETED && c.getEndDate() == null) {
            c.setEndDate(LocalDateTime.now());
        }

        CaseEntity saved = caseRepository.save(c);

        StatusHistoryEntity h = new StatusHistoryEntity();
        h.setCaseId(saved.getId());
        h.setPreviousStatus(from.name());
        h.setNewStatus(to.name());
        h.setChangedAt(LocalDateTime.now());
        h.setInitiatedBy(req.getInitiatedBy());

        historyRepository.save(h);

        // ✅ triggers (stub)
        List<String> triggers = new ArrayList<>();
        triggers.add("METRICS_RECALCULATED (stub)");
        triggers.add("NOTIFICATION_SENT (stub: log/email)");

        // ✅ оставляем и логи (как stub)
        log.info("Metrics stub: recalculated for caseId={}, status={}", saved.getId(), to);
        log.info("Notification stub: caseId={} status changed {} -> {}, initiatedBy={}",
                saved.getId(), from, to, req.getInitiatedBy());

        ChangeStatusResponse resp = new ChangeStatusResponse();
        resp.setCaseData(toResponse(saved));
        resp.setTriggers(triggers);
        return resp;
    }

    private void checkPreconditions(CaseEntity c, CaseStatus from, CaseStatus to, ChangeStatusRequest req) {

        if (to == CaseStatus.PROCEDURE_RUNNING) {
            boolean hasParties = !casePartyRepository
                    .findAllByCaseIdOrderByCreatedAtAsc(c.getId())
                    .isEmpty();

            if (!hasParties) {
                throw new ForbiddenStatusTransitionException(
                        "Cannot move to PROCEDURE_RUNNING without parties"
                );
            }
        }

        if (to == CaseStatus.COMPLETED) {
            boolean hasDocs = documentRepository.existsByCaseId(c.getId());

            if (!hasDocs) {
                throw new ForbiddenStatusTransitionException(
                        "Cannot move to COMPLETED without documents"
                );
            }
        }
    }

    private CaseResponse toResponse(CaseEntity e) {
        CaseResponse r = new CaseResponse();
        r.setId(e.getId());
        r.setCaseNumber(e.getCaseNumber());
        r.setProcedureType(e.getProcedureType().getCode());
        r.setStatus(e.getStatus());
        r.setStartDate(e.getStartDate());
        r.setEndDate(e.getEndDate());

        List<CasePartyShortResponse> parties = casePartyRepository
                .findAllByCaseIdOrderByCreatedAtAsc(e.getId())
                .stream()
                .map(CaseStatusService::toShortParty)
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
}