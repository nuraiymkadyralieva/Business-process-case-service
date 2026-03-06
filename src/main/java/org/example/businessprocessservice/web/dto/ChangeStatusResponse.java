package org.example.businessprocessservice.web.dto;

import java.util.List;

public class ChangeStatusResponse {
    private CaseResponse caseData;
    private List<String> triggers;

    public CaseResponse getCaseData() { return caseData; }
    public void setCaseData(CaseResponse caseData) { this.caseData = caseData; }

    public List<String> getTriggers() { return triggers; }
    public void setTriggers(List<String> triggers) { this.triggers = triggers; }
}