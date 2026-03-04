package org.example.businessprocessservice.web.controllers;

import jakarta.validation.Valid;
import org.example.businessprocessservice.service.roles.PartyRoleService;
import org.example.businessprocessservice.web.dto.PartyRoleRequest;
import org.example.businessprocessservice.web.dto.PartyRoleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/party-roles")
public class PartyRoleController {

    private final PartyRoleService partyRoleService;

    public PartyRoleController(PartyRoleService partyRoleService) {
        this.partyRoleService = partyRoleService;
    }

    @GetMapping
    public List<PartyRoleResponse> list() {
        return partyRoleService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyRoleResponse create(@Valid @RequestBody PartyRoleRequest req) {
        return partyRoleService.create(req);
    }
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        partyRoleService.deleteByCode(code);
    }
}