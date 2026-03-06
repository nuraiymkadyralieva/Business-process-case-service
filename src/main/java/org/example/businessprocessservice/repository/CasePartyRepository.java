package org.example.businessprocessservice.repository;

import org.example.businessprocessservice.domain.entity.CasePartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasePartyRepository extends JpaRepository<CasePartyEntity, Long> {

    List<CasePartyEntity> findAllByCaseIdOrderByCreatedAtAsc(Long caseId);

    // ✅ роль используется хотя бы где-то?
    boolean existsByRole_Id(Long roleId);
    boolean existsByCaseId(Long caseId);
}