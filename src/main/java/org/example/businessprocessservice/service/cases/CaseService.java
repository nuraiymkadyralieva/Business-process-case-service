package org.example.businessprocessservice.service.cases;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.web.dto.CaseResponse;
import org.example.businessprocessservice.web.dto.CreateCaseRequest;
import org.springframework.stereotype.Service;



import java.time.LocalDateTime;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
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

    public CaseResponse getCase(Long id) {
        CaseEntity e = caseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));
        return toResponse(e);
    }

    private static CaseResponse toResponse(CaseEntity e) {
        CaseResponse r = new CaseResponse();
        r.setId(e.getId());
        r.setCaseNumber(e.getCaseNumber());
        r.setProcedureType(e.getProcedureType());
        r.setStatus(e.getStatus());
        r.setStartDate(e.getStartDate());
        r.setEndDate(e.getEndDate());
        return r;
    }
}