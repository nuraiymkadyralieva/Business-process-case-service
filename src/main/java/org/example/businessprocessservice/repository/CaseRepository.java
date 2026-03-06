package org.example.businessprocessservice.repository;

import org.example.businessprocessservice.domain.entity.CaseEntity;
import org.example.businessprocessservice.domain.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {

    Optional<CaseEntity> findByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String caseNumber);

    // ✅ фильтр только по статусу
    Page<CaseEntity> findAllByStatus(CaseStatus status, Pageable pageable);

    // ✅ поиск по номеру (contains, без учёта регистра)
    Page<CaseEntity> findAllByCaseNumberContainingIgnoreCase(String caseNumber, Pageable pageable);

    // ✅ фильтр по статусу + поиск по номеру (contains)
    Page<CaseEntity> findAllByStatusAndCaseNumberContainingIgnoreCase(
            CaseStatus status,
            String caseNumber,
            Pageable pageable
    );

    // ✅ для запрета удаления ProcedureType (если ты это будешь делать):
    boolean existsByProcedureType_Code(String code);
}