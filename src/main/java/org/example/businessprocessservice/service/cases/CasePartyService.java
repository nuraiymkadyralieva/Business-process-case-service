package org.example.businessprocessservice.service.cases;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CasePartyEntity;
import org.example.businessprocessservice.domain.entity.PartyRoleEntity;
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CasePartyRepository;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.repository.PartyRoleRepository;
import org.example.businessprocessservice.web.dto.CasePartyResponse;
import org.example.businessprocessservice.web.dto.CreateCasePartyRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CasePartyService {

    private final CaseRepository caseRepository;
    private final CasePartyRepository partyRepository;
    private final PartyRoleRepository roleRepository;

    public CasePartyService(
            CaseRepository caseRepository,
            CasePartyRepository partyRepository,
            PartyRoleRepository roleRepository
    ) {
        this.caseRepository = caseRepository;
        this.partyRepository = partyRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public CasePartyResponse addParty(Long caseId, CreateCasePartyRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is required");
        if (req.getPartyType() == null) throw new IllegalArgumentException("partyType is required");

        if (req.getRoleCode() == null || req.getRoleCode().isBlank()) {
            throw new IllegalArgumentException("roleCode is required");
        }

        if (req.getExternalRef() == null || req.getExternalRef().isBlank()) {
            throw new IllegalArgumentException("externalRef is required");
        }

        // 404 если кейса нет
        caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        // Находим роль по коду (справочник)
        String roleCode = req.getRoleCode().trim().toUpperCase();
        PartyRoleEntity role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown roleCode: " + roleCode));

        CasePartyEntity e = new CasePartyEntity();
        e.setCaseId(caseId);
        e.setPartyType(req.getPartyType());
        e.setRole(role); // <-- теперь не String, а entity
        e.setExternalRef(req.getExternalRef().trim());
        e.setDisplayName(req.getDisplayName());

        CasePartyEntity saved = partyRepository.save(e);
        return toResponse(saved);
    }

    public List<CasePartyResponse> listParties(Long caseId) {
        caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        return partyRepository.findAllByCaseIdOrderByCreatedAtAsc(caseId)
                .stream()
                .map(CasePartyService::toResponse)
                .toList();
    }

    @Transactional
    public void deleteParty(Long caseId, Long partyId) {
        caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        CasePartyEntity e = partyRepository.findById(partyId)
                .orElseThrow(() -> new NotFoundException("Party not found: " + partyId));

        if (!e.getCaseId().equals(caseId)) {
            throw new IllegalArgumentException("Party " + partyId + " does not belong to case " + caseId);
        }

        partyRepository.delete(e);
    }

    private static CasePartyResponse toResponse(CasePartyEntity e) {
        CasePartyResponse r = new CasePartyResponse();
        r.setId(e.getId());
        r.setCaseId(e.getCaseId());
        r.setPartyType(e.getPartyType());

        // роль теперь entity -> в ответ отдаём код/имя
        if (e.getRole() != null) {
            r.setRoleCode(e.getRole().getCode());
            r.setRoleName(e.getRole().getName());
        }

        r.setExternalRef(e.getExternalRef());
        r.setDisplayName(e.getDisplayName());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}