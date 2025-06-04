package com.cdg.springjwt.controllers.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RefusDemandeeDTO {
    @NotBlank(message = "Le commentaire est obligatoire pour un refus")
    private String commentaire;
}