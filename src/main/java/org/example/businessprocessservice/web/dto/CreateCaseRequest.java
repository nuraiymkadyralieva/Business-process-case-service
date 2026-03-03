package org.example.businessprocessservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCaseRequest {

    @NotBlank
    private String caseNumber;

    @NotBlank
    private String procedureType;

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getProcedureType() { return procedureType; }
    public void setProcedureType(String procedureType) { this.procedureType = procedureType; }
}