package com.cdg.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "collaborateurs")
public class Collaborateur {

    @Id
    @Column(name = "colab_matricule", nullable = false)
    private String colabMatricule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiale_id", nullable = false)
    private Filiale filiale;

    @Column(name = "colab_nom")
    private String colabNom;

    @Column(name = "colab_prenom")
    private String colabPrenom;

    @Column(name = "domaine")
    private String domaine;

    @Column(name = "metier")
    private String metier;

    @Column(name = "maitrise")
    private String maitrise;

    @Column(name = "type_disponibilite")
    private String typeDisponibilite;

    @Column(name = "disponible")
    private Boolean disponible; // true = O, false = N

    @Column(name = "periode_disponibilite")
    private String periodeDisponibilite; // Ex: "Mai 2025", ou date de début/fin


    @Column(name = "rating")
    private String rating; // ou Integer si c’est un score numérique

    @Column(name = "cv")
    private String cv; // chemin fichier ou base64 ou lien


    @Column(name = "date_entree_filiale")
    private Date dateEntreeFiliale;

    @Column(name = "date_entree_group")
    private Date dateEntreeGroup;

    @Column(name = "contact_rh")
    private String contactRh;


}
