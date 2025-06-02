package com.cdg.springjwt.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@Table(name = "niveaux_maitrise")
public class NiveauMaitrise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "code_maitrise", unique = true, nullable = false)
    private String codeMaitrise;

    @Column(name = "designation_maitrise", nullable = false)
    private String designationMaitrise;

}
