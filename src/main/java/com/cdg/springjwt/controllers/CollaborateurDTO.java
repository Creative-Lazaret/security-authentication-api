package com.cdg.springjwt.controllers;

import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.models.Filiale;

import java.util.Optional;

public record CollaborateurDTO(
        String colabMatricule,
        String colabNom,
        String colabPrenom,
        String domaine,
        String metier,
        String maitrise,
        String typeDisponibilite,
        Boolean disponible,
        String periodeDisponibilite,
        Integer nombreMission,
        String rating,
        String cv,
        Integer ancienneteGroupe,
        Integer ancienneteFiliale,
        String filiale
) {
    public static CollaborateurDTO from(Collaborateur c) {
        return new CollaborateurDTO(
                c.getColabMatricule(),
                c.getColabNom(),
                c.getColabPrenom(),
                c.getDomaine(),
                c.getMetier(),
                c.getMaitrise(),
                c.getTypeDisponibilite(),
                c.getDisponible(),
                c.getPeriodeDisponibilite(),
                c.getNombreMission(),
                c.getRating(),
                c.getCv(),
                c.getAncienneteGroupe(),
                c.getAncienneteFiliale(),
                Optional.ofNullable(c.getFiliale())
                        .map(Filiale::getName)
                        .map(Enum::name)
                        .orElse(null)

        );
    }
}
