package org.example.businessprocessservice.web.dto;

import jakarta.validation.constraints.NotNull;
import org.example.businessprocessservice.domain.enums.CaseStatus;

public class ChangeStatusRequest {
    @NotNull(message = "newStatus is required")
    private CaseStatus newStatus;

    private String initiatedBy;



    // пока простая строка (позже подключим Security)


    public CaseStatus getNewStatus() { return newStatus; }
    public void setNewStatus(CaseStatus newStatus) { this.newStatus = newStatus; }

    public String getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(String initiatedBy) { this.initiatedBy = initiatedBy; }
}