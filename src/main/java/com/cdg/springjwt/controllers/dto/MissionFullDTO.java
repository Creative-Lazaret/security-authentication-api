package com.cdg.springjwt.controllers.dto;

import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.models.Filiale;
import com.cdg.springjwt.models.Mission;
import com.cdg.springjwt.models.Objectif;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public record MissionFullDTO(
        Long id,
        String code,
        String titre,
        String metier,
        String description,
        String domaine,
        LocalDate dateDebut,
        LocalDate dateFin,
        String statut,
        FilialeDTO filiale,
        List<ObjectifDTO> objectifs,
        List<CollaborateurDTO> ressources,
        LocalDate dateCreation,
        String creePar
) {
    public static MissionFullDTO from(Mission mission) {
        return new MissionFullDTO(
                mission.getId(),
                mission.getCode(),
                mission.getTitre(),
                mission.getMetier(),
                mission.getDescription(),
                mission.getDomaine(),
                mission.getDateDebut(),
                mission.getDateFin(),
                getStatus(mission),
                FilialeDTO.from(mission.getFiliale()),
                mission.getObjectifs().stream()
                        .map(ObjectifDTO::from)
                        .collect(Collectors.toList()),
                mission.getRessources().stream()
                        .map(CollaborateurDTO::from)
                        .collect(Collectors.toList()),
                mission.getDateCreation(),
                mission.getCreePar()
        );
    }

    private static String getStatus(Mission mission) {
        return switch (mission.getStatut()) {
            case OUVERTE -> "Ouverte";
            case EN_COURS -> "En cours";
            case EN_ATTENTE_APPROBABTION -> "En attente d'approbation";
            case TERMINEE -> "Terminée";
            default -> mission.getStatut().name();
        };
    }

    public record FilialeDTO(
            Long id,
            String name
    ) {
        public static FilialeDTO from(Filiale filiale) {
            return new FilialeDTO(
                    filiale.getId(),
                    filiale.getName().name()
            );
        }
    }

    public record ObjectifDTO(
            Long id,
            String description,
            boolean atteint
    ) {
        public static ObjectifDTO from(Objectif objectif) {
            return new ObjectifDTO(
                    objectif.getId(),
                    objectif.getDescription(),
                    objectif.isAtteint()
            );
        }
    }

    public record CollaborateurDTO(
            String matricule,
            String nom,
            String prenom,
            String domaine,
            String metier,
            String maitrise,
            String typeDisponibilite,
            Boolean disponible,
            String periodeDisponibilite,
            String rating,
            String cv,
            Date dateEntreeFiliale,
            Date dateEntreeGroup,
            String contactRh,
            FilialeDTO filiale
    ) {
        public static CollaborateurDTO from(Collaborateur collaborateur) {
            return new CollaborateurDTO(
                    collaborateur.getColabMatricule(),
                    collaborateur.getColabNom(),
                    collaborateur.getColabPrenom(),
                    collaborateur.getDomaine(),
                    collaborateur.getMetier(),
                    collaborateur.getMaitrise(),
                    collaborateur.getTypeDisponibilite(),
                    collaborateur.getDisponible(),
                    collaborateur.getPeriodeDisponibilite(),
                    collaborateur.getRating(),
                    collaborateur.getCv(),
                    collaborateur.getDateEntreeFiliale(),
                    collaborateur.getDateEntreeGroup(),
                    collaborateur.getContactRh(),
                    collaborateur.getFiliale() != null ? FilialeDTO.from(collaborateur.getFiliale()) : null
            );
        }
    }
}
