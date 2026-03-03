package org.example.businessprocessservice.repository;

import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {
    Optional<CaseEntity> findByCaseNumber(String caseNumber);
    boolean existsByCaseNumber(String caseNumber);
}