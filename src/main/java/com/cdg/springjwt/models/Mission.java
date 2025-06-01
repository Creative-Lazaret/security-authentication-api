package com.cdg.springjwt.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "missions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_code", unique = true, nullable = false)
    private String code; // ex: MIS001

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String metier;

    @Column(nullable = false)
    private String details;

    private String domaine;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EStatutMission statut = EStatutMission.OUVERTE;
//
//    @Column(name = "filiale_emettrice", nullable = false)
//    private String filiale; // SDS, MSE, MADEAEF, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiale_id", nullable = false)
    private Filiale filiale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborateur_id")
    private Collaborateur collaborateurAssigne;

    // Audit
    private LocalDate dateCreation;
    private String creePar;
}