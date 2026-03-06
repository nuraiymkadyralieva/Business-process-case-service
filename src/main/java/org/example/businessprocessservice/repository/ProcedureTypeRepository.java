package org.example.businessprocessservice.repository;

import org.example.businessprocessservice.domain.entity.ProcedureTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcedureTypeRepository extends JpaRepository<ProcedureTypeEntity, Long> {

    Optional<ProcedureTypeEntity> findByCode(String code);

    boolean existsByCode(String code);
}