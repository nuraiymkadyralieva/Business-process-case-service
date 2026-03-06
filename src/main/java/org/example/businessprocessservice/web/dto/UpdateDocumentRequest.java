package org.example.businessprocessservice.web.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class UpdateDocumentRequest {

    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String description;

    @Size(max = 100)
    private String documentNumber;

    private LocalDateTime issuedAt;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}