package org.example.businessprocessservice.web.dto;

import org.example.businessprocessservice.domain.enums.CaseStatus;

import java.time.LocalDateTime;

public class CaseResponse {
    private Long id;
    private String caseNumber;
    private String procedureType;
    private CaseStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getProcedureType() { return procedureType; }
    public void setProcedureType(String procedureType) { this.procedureType = procedureType; }

    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}