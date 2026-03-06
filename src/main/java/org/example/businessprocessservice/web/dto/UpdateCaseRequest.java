package org.example.businessprocessservice.web.dto;

public class UpdateCaseRequest {

    // оба поля опциональны — можно менять одно из них
    private String caseNumber;
    private String procedureType; // это CODE из справочника (например BANKRUPTCY)

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getProcedureType() { return procedureType; }
    public void setProcedureType(String procedureType) { this.procedureType = procedureType; }
}