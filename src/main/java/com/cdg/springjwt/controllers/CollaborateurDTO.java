package com.cdg.springjwt.controllers;

import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.models.Filiale;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
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
        String ancienneteGroupe,
        String ancienneteFiliale,
        String filiale,
        String contactRh,
        Date dateEntreeFiliale,
        Date entreEntreeGroup
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
                computeAnciente(c.getDateEntreeGroup()),
                computeAnciente(c.getDateEntreeFiliale()),
                Optional.ofNullable(c.getFiliale())
                        .map(Filiale::getName)
                        .map(Enum::name)
                        .orElse(null),
                c.getContactRh(),
                c.getDateEntreeFiliale(),
                c.getDateEntreeGroup()
        );
    }

    private static String computeAnciente(Date dateEntree) {
        // Conversion vers LocalDate
        LocalDate localDateEntree = dateEntree.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Date actuelle
        LocalDate aujourdHui = LocalDate.now();

        // Calcul de l’ancienneté
        Period anciente = Period.between(localDateEntree, aujourdHui);
        return anciente.getYears() + " an " + anciente.getMonths() +" mois";
    }
}
