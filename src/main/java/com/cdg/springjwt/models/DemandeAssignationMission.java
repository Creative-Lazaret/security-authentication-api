package com.cdg.springjwt.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "demande_assignation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeAssignationMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Mission mission;

    @ManyToOne(optional = false)
    private Collaborateur collaborateur;

    @ManyToOne(optional = false)
    private Filiale filialeDemandeuse; // Filiale de la mission qui a demandé l'assignation et dans laquelle la mission sera exectuee'

     @ManyToOne(optional = true)
    private Filiale filialeReceptrice; // Filiale du collaborateur

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(length = 1000)
    private String commentaire; // facultatif

    private LocalDateTime dateCreation;
    private LocalDateTime dateDecision;

    @Column(nullable = false)
    private String creePar;
}
