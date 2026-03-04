package org.example.businessprocessservice.web.dto;

import org.example.businessprocessservice.domain.enums.CaseStatus;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "caseNumber",
        "procedureType",
        "status",
        "startDate",
        "endDate",
        "parties"
})
public class CaseResponse {
    private Long id;
    private String caseNumber;
    private String procedureType;
    private CaseStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // ✅ краткие участники (3 поля)
    private List<CasePartyShortResponse> parties;

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

    public List<CasePartyShortResponse> getParties() { return parties; }
    public void setParties(List<CasePartyShortResponse> parties) { this.parties = parties; }
}