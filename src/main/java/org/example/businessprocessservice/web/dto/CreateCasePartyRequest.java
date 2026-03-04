package org.example.businessprocessservice.web.dto;

import org.example.businessprocessservice.domain.enums.PartyType;

public class CreateCasePartyRequest {
    private PartyType partyType;
    private String roleCode;
    private String externalRef;
    private String displayName;

    public PartyType getPartyType() { return partyType; }
    public void setPartyType(PartyType partyType) { this.partyType = partyType; }

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}