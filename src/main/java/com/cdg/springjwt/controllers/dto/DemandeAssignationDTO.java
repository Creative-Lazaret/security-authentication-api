package com.cdg.springjwt.controllers.dto;

import com.cdg.springjwt.models.DemandeAssignationMission;
import com.cdg.springjwt.models.StatutDemande;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class DemandeAssignationDTO {
    
    private Long id;
    private StatutDemande statut;
    private String commentaire;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDecision;
    private String creePar;
    
    // Mission info
    private Long missionId;
    private String missionCode;
    private String missionTitre;
    private String missionMetier;
    private String missionDomaine;
    private String missionDescription;

    // Collaborateur info
    private String collaborateurMatricule;
    private String collaborateurNom;
    private String collaborateurPrenom;
    private String collaborateurMetier;
    
    // Filiales info
    private String filialeDemandeuse;
    private String filialeReceptrice;

    public static DemandeAssignationDTO from(DemandeAssignationMission demande) {
        return DemandeAssignationDTO.builder()
                .id(demande.getId())
                .statut(demande.getStatut())
                .commentaire(demande.getCommentaire())
                .dateCreation(demande.getDateCreation())
                .dateDecision(demande.getDateDecision())
                .creePar(demande.getCreePar())
                
                .missionId(demande.getMission().getId())
                .missionCode(demande.getMission().getCode())
                .missionTitre(demande.getMission().getTitre())
                .missionMetier(demande.getMission().getMetier())
                .missionDomaine(demande.getMission().getDomaine())
                .missionDescription(demande.getMission().getDescription())
                
                .collaborateurMatricule(demande.getCollaborateur().getColabMatricule())
                .collaborateurNom(demande.getCollaborateur().getColabNom())
                .collaborateurPrenom(demande.getCollaborateur().getColabPrenom())
                .collaborateurMetier(demande.getCollaborateur().getMetier())
                
                .filialeDemandeuse(demande.getFilialeDemandeuse().getName().name())
                .filialeReceptrice(demande.getFilialeReceptrice().getName().name())
                .build();
    }
}