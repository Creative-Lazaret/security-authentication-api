package com.cdg.springjwt.controllers;

import com.cdg.springjwt.MissionService;
import com.cdg.springjwt.models.EFiliale;
import com.cdg.springjwt.models.EStatutMission;
import com.cdg.springjwt.models.Mission;
import com.cdg.springjwt.repository.MissionRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/missions")
public class MissionController {


    @Autowired
    private MissionRepository missionRepository;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<MissionFullDTO> getAllMissions(
            @RequestParam(required = false) String filiale,
            @RequestParam(required = false) String metier,
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String titre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return missionRepository.findAll((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (titre != null && !titre.isBlank()) {
                        String pattern = "%" + titre.toLowerCase() + "%";
                        predicates.add(cb.like(cb.lower(root.get("titre")), pattern));
                    }

                    if (filiale != null && !filiale.isBlank()) {
                        try {
                            EFiliale eFiliale = EFiliale.valueOf(filiale);
                            predicates.add(cb.equal(root.get("filiale").get("name"), eFiliale));
                        } catch (IllegalArgumentException ex) {
                            System.out.println("⚠️ Filiale inconnue : " + filiale);
                        }
                    }

                    if (metier != null && !metier.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("metier")), metier.toLowerCase()));
                    }

                    if (domaine != null && !domaine.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("domaine")), domaine.toLowerCase()));
                    }

                    if (statut != null && !statut.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("statut")), statut.toLowerCase()));
                    }

                    if (dateDebut != null && !dateDebut.isBlank()) {
                        try {
                            LocalDate debut = LocalDate.parse(dateDebut); // Format: YYYY-MM-DD
                            predicates.add(cb.greaterThanOrEqualTo(root.get("dateDebut"), debut));
                        } catch (IllegalArgumentException ex) {
                            System.out.println("⚠️ Format de date début invalide : " + dateDebut);
                        }
                    }

                    if (dateFin != null && !dateFin.isBlank()) {
                        try {
                            LocalDate fin = LocalDate.parse(dateFin); // Format: YYYY-MM-DD
                            predicates.add(cb.lessThanOrEqualTo(root.get("dateFin"), fin));
                        } catch (IllegalArgumentException ex) {
                            System.out.println("⚠️ Format de date fin invalide : " + dateFin);
                        }
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }, pageable)
                .map(MissionFullDTO::from);
    }

}
