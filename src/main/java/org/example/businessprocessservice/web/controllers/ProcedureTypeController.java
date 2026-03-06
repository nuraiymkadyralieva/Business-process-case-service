package org.example.businessprocessservice.web.controllers;

import jakarta.validation.Valid;
import org.example.businessprocessservice.service.procedure.ProcedureTypeService;
import org.example.businessprocessservice.web.dto.ProcedureTypeRequest;
import org.example.businessprocessservice.web.dto.ProcedureTypeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procedure-types")
public class ProcedureTypeController {

    private final ProcedureTypeService service;

    public ProcedureTypeController(ProcedureTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProcedureTypeResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedureTypeResponse create(@Valid @RequestBody ProcedureTypeRequest req) {
        return service.create(req);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        service.deleteByCode(code);
    }
}