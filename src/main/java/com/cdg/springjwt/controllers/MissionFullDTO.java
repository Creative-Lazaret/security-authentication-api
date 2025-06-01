package com.cdg.springjwt.controllers;


import com.cdg.springjwt.models.Filiale;
import com.cdg.springjwt.models.Mission;
import com.cdg.springjwt.models.EStatutMission;

import java.time.LocalDate;

public record MissionFullDTO(
        Long id,
        String code,
        String titre,
        String metier,
        String details,
        String domaine,
        LocalDate dateDebut,
        LocalDate dateFin,
        String statut,
        FilialeDTO filiale,
        LocalDate dateCreation,
        String creePar
) {
    public static MissionFullDTO from(Mission mission) {
        return new MissionFullDTO(
                mission.getId(),
                mission.getCode(),
                mission.getTitre(),
                mission.getMetier(),
                mission.getDetails(),
                mission.getDomaine(),
                mission.getDateDebut(),
                mission.getDateFin(),
                getStatus(mission),
                FilialeDTO.from(mission.getFiliale()),
                mission.getDateCreation(),
                mission.getCreePar()
        );
    }

    private static String getStatus(Mission mission) {
        return mission.getStatut().equals(EStatutMission.EN_COURS) ? "En cours" : mission.getStatut().name();
    }

    public record FilialeDTO(
            Long id,
            String name
    ) {
        public static FilialeDTO from(Filiale filiale) {
            return new FilialeDTO(
                    filiale.getId(),
                    filiale.getName().name() // ← attention ici
            );
        }
    }
}
