package org.example.businessprocessservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PartyRoleRequest {

    @NotBlank(message = "code must not be blank")
    @Size(max = 50, message = "code must be <= 50 chars")
    private String code;

    @NotBlank(message = "name must not be blank")
    @Size(max = 150, message = "name must be <= 150 chars")
    private String name;

    @Size(max = 500, message = "description must be <= 500 chars")
    private String description;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }}