package org.example.businessprocessservice.service.roles;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.PartyRoleEntity;
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CasePartyRepository;
import org.example.businessprocessservice.repository.PartyRoleRepository;
import org.example.businessprocessservice.web.dto.PartyRoleRequest;
import org.example.businessprocessservice.web.dto.PartyRoleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartyRoleService {

    private final PartyRoleRepository roleRepository;
    private final CasePartyRepository casePartyRepository;

    public PartyRoleService(PartyRoleRepository roleRepository,
                            CasePartyRepository casePartyRepository) {
        this.roleRepository = roleRepository;
        this.casePartyRepository = casePartyRepository;
    }

    public List<PartyRoleResponse> list() {
        return roleRepository.findAll()
                .stream()
                .map(PartyRoleService::toResponse)
                .toList();
    }

    @Transactional
    public PartyRoleResponse create(PartyRoleRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is required");

        String code = normalizeCode(req.getCode());
        String name = req.getName() == null ? "" : req.getName().trim();
        String desc = req.getDescription() == null ? null : req.getDescription().trim();

        if (code.isBlank()) throw new IllegalArgumentException("code is required");
        if (name.isBlank()) throw new IllegalArgumentException("name is required");

        roleRepository.findByCode(code).ifPresent(r -> {
            throw new IllegalArgumentException("Role with code already exists: " + code);
        });

        PartyRoleEntity e = new PartyRoleEntity();
        e.setCode(code);
        e.setName(name);
        e.setDescription(desc);

        PartyRoleEntity saved = roleRepository.save(e);
        return toResponse(saved);
    }

    public PartyRoleEntity getByCodeOrThrow(String code) {
        String normalized = normalizeCode(code);
        return roleRepository.findByCode(normalized)
                .orElseThrow(() -> new NotFoundException("Party role not found: " + normalized));
    }

    @Transactional
    public void deleteByCode(String code) {
        PartyRoleEntity role = getByCodeOrThrow(code);

        // ✅ запрет удаления, если роль используется в case_parties
        if (casePartyRepository.existsByRole_Id(role.getId())) {
            throw new IllegalStateException(
                    "Cannot delete role " + role.getCode() + ": it is used in case_parties"
            );
        }

        roleRepository.delete(role);
    }

    private static String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase();
    }

    private static PartyRoleResponse toResponse(PartyRoleEntity e) {
        PartyRoleResponse r = new PartyRoleResponse();
        r.setId(e.getId());
        r.setCode(e.getCode());
        r.setName(e.getName());
        r.setDescription(e.getDescription());
        return r;
    }
}