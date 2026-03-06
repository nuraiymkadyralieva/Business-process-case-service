package org.example.businessprocessservice.service.procedure;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.ProcedureTypeEntity;
import org.example.businessprocessservice.repository.ProcedureTypeRepository;
import org.example.businessprocessservice.web.dto.ProcedureTypeRequest;
import org.example.businessprocessservice.web.dto.ProcedureTypeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcedureTypeService {

    private final ProcedureTypeRepository procedureTypeRepository;

    public ProcedureTypeService(ProcedureTypeRepository procedureTypeRepository) {
        this.procedureTypeRepository = procedureTypeRepository;
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

        if (procedureTypeRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Procedure type already exists: " + code);
        }

        ProcedureTypeEntity e = new ProcedureTypeEntity();
        e.setCode(code);
        e.setName(req.getName() == null ? "" : req.getName().trim());
        e.setDescription(req.getDescription() == null ? null : req.getDescription().trim());

        ProcedureTypeEntity saved = procedureTypeRepository.save(e);
        return toResponse(saved);
    }

    @Transactional
    public void deleteByCode(String codeRaw) {
        String code = normalizeCode(codeRaw);
        if (code.isBlank()) throw new IllegalArgumentException("code is required");

        ProcedureTypeEntity e = procedureTypeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Procedure type not found: " + code));

        // Позже добавим проверку "нельзя удалить если используется в cases"
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