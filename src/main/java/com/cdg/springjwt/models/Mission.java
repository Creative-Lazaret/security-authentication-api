package com.cdg.springjwt.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    private String description;

    private String domaine;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EStatutMission statut = EStatutMission.OUVERTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiale_id", nullable = false)
    private Filiale filiale;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    private Set<Objectif> objectifs = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "mission_ressources",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "colab_matricule")
    )
    private Set<Collaborateur> ressources = new HashSet<>();

    // Audit
    private LocalDate dateCreation;
    private String creePar;
}