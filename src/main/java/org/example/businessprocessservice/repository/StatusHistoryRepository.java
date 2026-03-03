package org.example.businessprocessservice.repository;

import org.example.businessprocessservice.domain.entity.StatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistoryEntity, Long> {
    List<StatusHistoryEntity> findByCaseIdOrderByChangedAtAsc(Long caseId);
}