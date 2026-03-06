package org.example.businessprocessservice.web.dto;

public class UpdatePartyRoleRequest {
    // code НЕ обновляем, поэтому тут его нет
    private String name;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}