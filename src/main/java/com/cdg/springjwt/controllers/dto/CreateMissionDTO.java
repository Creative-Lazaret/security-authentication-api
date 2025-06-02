package com.cdg.springjwt.controllers.dto;


import com.cdg.springjwt.models.EStatutMission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CreateMissionDTO {

    // Code is now optional - will be auto-generated if not provided
    private String code;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le métier est obligatoire")
    private String metier;

    private String description;

    private String domaine;

//    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

//    private EStatutMission statut = EStatutMission.OUVERTE;

    @NotNull(message = "La filiale est obligatoire")
    private Long filialeId;

    private Set<Long> ressourceIds;
}