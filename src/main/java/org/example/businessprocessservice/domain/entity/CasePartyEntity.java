package org.example.businessprocessservice.domain.entity;

import jakarta.persistence.*;
import org.example.businessprocessservice.domain.enums.PartyType;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_parties")
public class CasePartyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false, length = 30)
    private PartyType partyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_role_id", nullable = false)
    private PartyRoleEntity role;

    @Column(name = "external_ref", nullable = false, length = 120)
    private String externalRef;

    @Column(name = "display_name", length = 200)
    private String displayName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // getters/setters
    public Long getId() { return id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public PartyType getPartyType() { return partyType; }
    public void setPartyType(PartyType partyType) { this.partyType = partyType; }
    public PartyRoleEntity getRole() {
        return role;
    }
    public void setRole(PartyRoleEntity role) {
        this.role = role;
    }

    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}