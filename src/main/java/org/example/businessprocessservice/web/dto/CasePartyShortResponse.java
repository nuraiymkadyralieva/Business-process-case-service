package org.example.businessprocessservice.web.dto;

import org.example.businessprocessservice.domain.enums.PartyType;

public class CasePartyShortResponse {

    private String roleCode;      // DEBTOR / CREDITOR
    private PartyType partyType;  // PERSON / LEGAL_ENTITY
    private String displayName;   // Иванов Иван / ООО Ромашка

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    public PartyType getPartyType() { return partyType; }
    public void setPartyType(PartyType partyType) { this.partyType = partyType; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}