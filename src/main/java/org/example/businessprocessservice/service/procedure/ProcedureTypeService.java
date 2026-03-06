package org.example.businessprocessservice.service.procedure;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.ProcedureTypeEntity;
import org.example.businessprocessservice.exception.ForbiddenStatusTransitionException;
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.repository.ProcedureTypeRepository;
import org.example.businessprocessservice.web.dto.ProcedureTypeRequest;
import org.example.businessprocessservice.web.dto.ProcedureTypeResponse;
import org.example.businessprocessservice.web.dto.UpdateProcedureTypeRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcedureTypeService {

    private final ProcedureTypeRepository procedureTypeRepository;
    private final CaseRepository caseRepository;

    public ProcedureTypeService(ProcedureTypeRepository procedureTypeRepository,
                                CaseRepository caseRepository) {
        this.procedureTypeRepository = procedureTypeRepository;
        this.caseRepository = caseRepository;
    }

    public List<ProcedureTypeResponse> list() {
        return procedureTypeRepository.findAll()
                .stream()
                .map(ProcedureTypeService::toResponse)
                .toList();
    }

    @Transactional
    public ProcedureTypeResponse create(ProcedureTypeRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is required");

        String code = normalizeCode(req.getCode());
        if (code.isBlank()) throw new IllegalArgumentException("code is required");

        String name = req.getName() == null ? "" : req.getName().trim();
        if (name.isBlank()) throw new IllegalArgumentException("name is required");

        if (procedureTypeRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Procedure type already exists: " + code);
        }

        ProcedureTypeEntity e = new ProcedureTypeEntity();
        e.setCode(code);
        e.setName(name);
        e.setDescription(req.getDescription() == null ? null : req.getDescription().trim());

        return toResponse(procedureTypeRepository.save(e));
    }

    // ✅ ДОБАВИЛИ: UPDATE по code (code НЕ меняем)
    @Transactional
    public ProcedureTypeResponse updateByCode(String codeRaw, UpdateProcedureTypeRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is required");

        String code = normalizeCode(codeRaw);
        if (code.isBlank()) throw new IllegalArgumentException("code is required");

        ProcedureTypeEntity e = procedureTypeRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Procedure type not found: " + code));

        if (req.getName() != null) {
            String name = req.getName().trim();
            if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
            e.setName(name);
        }

        if (req.getDescription() != null) {
            e.setDescription(req.getDescription().trim());
        }

        return toResponse(procedureTypeRepository.save(e));
    }

    @Transactional
    public void deleteByCode(String codeRaw) {
        String code = normalizeCode(codeRaw);
        if (code.isBlank()) throw new IllegalArgumentException("code is required");

        ProcedureTypeEntity e = procedureTypeRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Procedure type not found: " + code));

        // ✅ ДОБАВИЛИ: запрет удаления, если тип используется в cases
        if (caseRepository.existsByProcedureType_Code(code)) {
            throw new ForbiddenStatusTransitionException(
                    "Cannot delete procedure type " + code + ": it is used in cases"
            );
        }

        procedureTypeRepository.delete(e);
    }

    private static String normalizeCode(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private static ProcedureTypeResponse toResponse(ProcedureTypeEntity e) {
        ProcedureTypeResponse r = new ProcedureTypeResponse();
        r.setId(e.getId());
        r.setCode(e.getCode());
        r.setName(e.getName());
        r.setDescription(e.getDescription());
        return r;
    }
}