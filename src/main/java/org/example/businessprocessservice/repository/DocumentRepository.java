package org.example.businessprocessservice.repository;

import org.example.businessprocessservice.domain.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    List<DocumentEntity> findAllByCaseIdOrderByCreatedAtAsc(Long caseId);

    boolean existsByCaseId(Long caseId);

    Optional<DocumentEntity> findByIdAndCaseId(Long id, Long caseId);
}