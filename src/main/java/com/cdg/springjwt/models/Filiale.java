package com.cdg.springjwt.models;

import jakarta.persistence.*;

@Entity
@Table(name = "filiales")
public class Filiale {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EFiliale name;

    public Filiale(String name) {
        this.name = EFiliale.valueOf(name);
    }

    public Filiale() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EFiliale getName() {
        return name;
    }

    public void setName(String filiale) {
        this.name = EFiliale.valueOf(filiale);
    }
}
