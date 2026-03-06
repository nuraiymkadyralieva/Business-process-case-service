package org.example.businessprocessservice.service.documents;

import jakarta.transaction.Transactional;
import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.entity.DocumentEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.example.businessprocessservice.exception.ForbiddenStatusTransitionException; // ✅ ДОБАВИЛИ ИМПОРТ
import org.example.businessprocessservice.exception.NotFoundException;
import org.example.businessprocessservice.repository.CaseRepository;
import org.example.businessprocessservice.repository.DocumentRepository;
import org.example.businessprocessservice.web.dto.CreateDocumentRequest;
import org.example.businessprocessservice.web.dto.DocumentResponse;
import org.example.businessprocessservice.web.dto.UpdateDocumentRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CaseRepository caseRepository;

    public DocumentService(DocumentRepository documentRepository, CaseRepository caseRepository) {
        this.documentRepository = documentRepository;
        this.caseRepository = caseRepository;
    }

    public List<DocumentResponse> list(Long caseId) {
        return documentRepository.findAllByCaseIdOrderByCreatedAtAsc(caseId)
                .stream()
                .map(DocumentService::toResponse)
                .toList();
    }

    @Transactional
    public DocumentResponse create(Long caseId, CreateDocumentRequest req) {
        CaseEntity c = caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        forbidIfLocked(c);

        DocumentEntity e = new DocumentEntity();
        e.setCaseId(caseId);
        e.setType(normalizeCode(req.getType()));
        e.setTitle(req.getTitle().trim());
        e.setDescription(req.getDescription() == null ? null : req.getDescription().trim());
        e.setDocumentNumber(req.getDocumentNumber() == null ? null : req.getDocumentNumber().trim());
        e.setIssuedAt(req.getIssuedAt());
        e.setCreatedBy(req.getCreatedBy() == null ? null : req.getCreatedBy().trim());

        return toResponse(documentRepository.save(e));
    }

    @Transactional
    public DocumentResponse update(Long caseId, Long docId, UpdateDocumentRequest req) {
        CaseEntity c = caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        forbidIfLocked(c);

        DocumentEntity d = documentRepository.findByIdAndCaseId(docId, caseId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + docId));

        // ✅ type НЕ трогаем

        if (req.getTitle() != null) d.setTitle(req.getTitle().trim());
        if (req.getDescription() != null) d.setDescription(req.getDescription().trim());
        if (req.getDocumentNumber() != null) d.setDocumentNumber(req.getDocumentNumber().trim());
        if (req.getIssuedAt() != null) d.setIssuedAt(req.getIssuedAt());

        return toResponse(documentRepository.save(d));
    }

    @Transactional
    public void delete(Long caseId, Long docId) {
        CaseEntity c = caseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseId));

        forbidIfLocked(c);

        DocumentEntity d = documentRepository.findByIdAndCaseId(docId, caseId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + docId));

        documentRepository.delete(d);
    }

    private void forbidIfLocked(CaseEntity c) {
        if (c.getStatus() == CaseStatus.COMPLETED || c.getStatus() == CaseStatus.ARCHIVED) {

            // ✅ ИЗМЕНЕНИЕ ЗДЕСЬ:
            // раньше было: throw new IllegalStateException(...)
            // теперь кидаем ForbiddenStatusTransitionException, чтобы GlobalExceptionHandler вернул 409 (Conflict)
            throw new ForbiddenStatusTransitionException(
                    "Documents cannot be modified for status: " + c.getStatus()
            );
        }
    }

    private static String normalizeCode(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private static DocumentResponse toResponse(DocumentEntity e) {
        DocumentResponse r = new DocumentResponse();
        r.setId(e.getId());
        r.setCaseId(e.getCaseId());
        r.setType(e.getType());
        r.setTitle(e.getTitle());
        r.setDescription(e.getDescription());
        r.setDocumentNumber(e.getDocumentNumber());
        r.setIssuedAt(e.getIssuedAt());
        r.setCreatedAt(e.getCreatedAt());
        r.setCreatedBy(e.getCreatedBy());
        return r;
    }
}