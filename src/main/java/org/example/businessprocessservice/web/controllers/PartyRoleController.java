package org.example.businessprocessservice.web.controllers;

import jakarta.validation.Valid;
import org.example.businessprocessservice.service.roles.PartyRoleService;
import org.example.businessprocessservice.web.dto.PartyRoleRequest;
import org.example.businessprocessservice.web.dto.PartyRoleResponse;
import org.example.businessprocessservice.web.dto.UpdatePartyRoleRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/party-roles")
public class PartyRoleController {

    private final PartyRoleService service;

    public PartyRoleController(PartyRoleService service) {
        this.service = service;
    }

    @GetMapping
    public List<PartyRoleResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyRoleResponse create(@Valid @RequestBody PartyRoleRequest req) {
        return service.create(req);
    }

    // ✅ UPDATE (code не меняем)
    @PatchMapping("/{code}")
    public PartyRoleResponse update(@PathVariable String code,
                                    @RequestBody UpdatePartyRoleRequest req) {
        return service.updateByCode(code, req);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        service.deleteByCode(code);
    }
}