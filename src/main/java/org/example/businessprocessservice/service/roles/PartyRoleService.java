package org.example.businessprocessservice.service.roles;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.PartyRoleEntity;
import org.example.businessprocessservice.exception.ForbiddenStatusTransitionException;
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CasePartyRepository;
import org.example.businessprocessservice.repository.PartyRoleRepository;
import org.example.businessprocessservice.web.dto.PartyRoleRequest;
import org.example.businessprocessservice.web.dto.PartyRoleResponse;
import org.example.businessprocessservice.web.dto.UpdatePartyRoleRequest;
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
        if (code.isBlank()) throw new IllegalArgumentException("code is required");

        String name = req.getName() == null ? "" : req.getName().trim();
        if (name.isBlank()) throw new IllegalArgumentException("name is required");

        if (roleRepository.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Role with code already exists: " + code);
        }

        PartyRoleEntity e = new PartyRoleEntity();
        e.setCode(code);
        e.setName(name);
        e.setDescription(req.getDescription() == null ? null : req.getDescription().trim());

        return toResponse(roleRepository.save(e));
    }

    private PartyRoleEntity getByCodeOrThrow(String codeRaw) {
        String code = normalizeCode(codeRaw);
        return roleRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Party role not found: " + code));
    }

    // ✅ ДОБАВИЛИ: UPDATE по code (меняем только name/description)
    @Transactional
    public PartyRoleResponse updateByCode(String codeRaw, UpdatePartyRoleRequest req) {
        if (req == null) throw new IllegalArgumentException("Request body is required");

        PartyRoleEntity role = getByCodeOrThrow(codeRaw);

        boolean changed = false;

        if (req.getName() != null) {
            String name = req.getName().trim();
            if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
            role.setName(name);
            changed = true;
        }

        if (req.getDescription() != null) {
            role.setDescription(req.getDescription().trim());
            changed = true;
        }

        if (!changed) {
            throw new IllegalArgumentException("Nothing to update");
        }

        return toResponse(roleRepository.save(role));
    }

    @Transactional
    public void deleteByCode(String codeRaw) {
        PartyRoleEntity role = getByCodeOrThrow(codeRaw);

        // ✅ запрет удаления, если роль используется
        if (casePartyRepository.existsByRole_Id(role.getId())) {
            // чтобы было 409, а не 500
            throw new ForbiddenStatusTransitionException(
                    "Cannot delete role " + role.getCode() + ": it is used in case_parties"
            );
        }

        roleRepository.delete(role);
    }

    private static String normalizeCode(String s) {
        return s == null ? "" : s.trim().toUpperCase();
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