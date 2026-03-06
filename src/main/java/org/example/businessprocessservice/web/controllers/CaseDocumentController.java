package org.example.businessprocessservice.web.controllers;

import jakarta.validation.Valid;
import org.example.businessprocessservice.service.documents.DocumentService;
import org.example.businessprocessservice.web.dto.CreateDocumentRequest;
import org.example.businessprocessservice.web.dto.DocumentResponse;
import org.example.businessprocessservice.web.dto.UpdateDocumentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/documents")
public class CaseDocumentController {

    private final DocumentService service;

    public CaseDocumentController(DocumentService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentResponse> list(@PathVariable Long caseId) {
        return service.list(caseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse create(@PathVariable Long caseId, @Valid @RequestBody CreateDocumentRequest req) {
        return service.create(caseId, req);
    }

    @PatchMapping("/{docId}")
    public DocumentResponse update(@PathVariable Long caseId, @PathVariable Long docId,
                                   @Valid @RequestBody UpdateDocumentRequest req) {
        return service.update(caseId, docId, req);
    }

    @DeleteMapping("/{docId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long caseId, @PathVariable Long docId) {
        service.delete(caseId, docId);
    }
}