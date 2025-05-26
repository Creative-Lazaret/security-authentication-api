package com.cdg.springjwt.controllers;

import com.cdg.springjwt.models.Domaine;
import com.cdg.springjwt.models.Metier;
import com.cdg.springjwt.models.TypeDisponibilite;
import com.cdg.springjwt.repository.DomaineRepository;
import com.cdg.springjwt.repository.FilialeRepository;
import com.cdg.springjwt.repository.MetierRepository;
import com.cdg.springjwt.repository.TypeDisponibiliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/parametrage")
public class ParametrageController {

    @Autowired
    private DomaineRepository domaineRepo;

    @Autowired
    private MetierRepository metierRepo;

    @Autowired
    private TypeDisponibiliteRepository typeDispoRepo;

    @Autowired
    private FilialeRepository filialeRepo;

    @GetMapping("/domaines")
    public List<String> getDomaines() {
        return domaineRepo.findAll().stream().map(Domaine::getLibelle).toList();
    }

    @GetMapping("/metiers")
    public List<String> getMetiers() {
        return metierRepo.findAll().stream().map(Metier::getLibelle).toList();
    }

    @GetMapping("/type-disponibilites")
    public List<String> getTypesDisponibilite() {
        return typeDispoRepo.findAll().stream().map(TypeDisponibilite::getLibelle).toList();
    }

    @GetMapping("/filiales")
    public List<String> getFiliales() {
        return filialeRepo.findAll().stream().map(filiale -> filiale.getName().name()).toList();
    }
}
