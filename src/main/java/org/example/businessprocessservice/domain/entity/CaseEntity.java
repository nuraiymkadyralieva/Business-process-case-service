package org.example.businessprocessservice.domain.entity;

import jakarta.persistence.*;

import org.example.businessprocessservice.domain.enums.CaseStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "cases")
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_number", nullable = false, unique = true, length = 100)
    private String caseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_type", referencedColumnName = "code", nullable = false)
    private ProcedureTypeEntity procedureType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CaseStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public CaseEntity() {}

    // --- getters/setters ---
    public Long getId() { return id; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public ProcedureTypeEntity getProcedureType() { return procedureType; }
    public void setProcedureType(ProcedureTypeEntity procedureType) { this.procedureType = procedureType; }

    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}