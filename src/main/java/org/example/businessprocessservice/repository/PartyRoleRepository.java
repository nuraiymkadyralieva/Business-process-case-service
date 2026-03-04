package org.example.businessprocessservice.repository;

import org.example.businessprocessservice.domain.entity.PartyRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRoleRepository extends JpaRepository<PartyRoleEntity, Long> {
    Optional<PartyRoleEntity> findByCode(String code);
}