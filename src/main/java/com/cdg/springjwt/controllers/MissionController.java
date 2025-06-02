package com.cdg.springjwt.controllers;

import com.cdg.springjwt.services.MissionCodeGeneratorService;
import com.cdg.springjwt.models.EFiliale;
import com.cdg.springjwt.models.EStatutMission;
import com.cdg.springjwt.models.Mission;
import com.cdg.springjwt.models.Filiale;
import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.repository.MissionRepository;
import com.cdg.springjwt.repository.FilialeRepository;
import com.cdg.springjwt.repository.CollaborateurRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private FilialeRepository filialeRepository;

    @Autowired
    private CollaborateurRepository collaborateurRepository;

    @Autowired
    private MissionCodeGeneratorService codeGeneratorService;

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
                            LocalDate debut = LocalDate.parse(dateDebut);
                            predicates.add(cb.greaterThanOrEqualTo(root.get("dateDebut"), debut));
                        } catch (IllegalArgumentException ex) {
                            System.out.println("⚠️ Format de date début invalide : " + dateDebut);
                        }
                    }

                    if (dateFin != null && !dateFin.isBlank()) {
                        try {
                            LocalDate fin = LocalDate.parse(dateFin);
                            predicates.add(cb.lessThanOrEqualTo(root.get("dateFin"), fin));
                        } catch (IllegalArgumentException ex) {
                            System.out.println("⚠️ Format de date fin invalide : " + dateFin);
                        }
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }, pageable)
                .map(MissionFullDTO::from);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createMission(@Valid @RequestBody CreateMissionDTO createMissionDTO,
                                           Authentication authentication) {
        try {
            // Generate code if not provided
            String missionCode = createMissionDTO.getCode();
            if (missionCode == null || missionCode.isBlank()) {
                missionCode = codeGeneratorService.generateMissionCode();
            } else {
                // If code is provided, check for duplicates
                if (missionRepository.existsByCode(missionCode)) {
                    return ResponseEntity.badRequest()
                            .body("Une mission avec ce code existe déjà: " + missionCode);
                }
            }

            // Récupérer la filiale
            Filiale filiale = filialeRepository.findById(createMissionDTO.getFilialeId())
                    .orElseThrow(() -> new RuntimeException("Filiale introuvable avec l'ID: " + createMissionDTO.getFilialeId()));

            // Créer la mission
            Mission mission = Mission.builder()
                    .code(missionCode)
                    .titre(createMissionDTO.getTitre())
                    .metier(createMissionDTO.getMetier())
                    .description(createMissionDTO.getDescription())
                    .domaine(createMissionDTO.getDomaine())
                    .dateDebut(createMissionDTO.getDateDebut())
                    .dateFin(createMissionDTO.getDateFin())
                    .statut(createMissionDTO.getStatut() != null ? createMissionDTO.getStatut() : EStatutMission.OUVERTE)
                    .filiale(filiale)
                    .dateCreation(LocalDate.now())
                    .creePar(authentication.getName())
                    .objectifs(new HashSet<>())
                    .ressources(new HashSet<>())
                    .build();

            // Ajouter les ressources si spécifiées
            if (createMissionDTO.getRessourceIds() != null && !createMissionDTO.getRessourceIds().isEmpty()) {
                Set<Collaborateur> ressources = new HashSet<>();
                for (Long ressourceId : createMissionDTO.getRessourceIds()) {
                    collaborateurRepository.findById(ressourceId)
                            .ifPresent(ressources::add);
                }
                mission.setRessources(ressources);
            }

            // Sauvegarder la mission
            Mission savedMission = missionRepository.save(mission);

            // Retourner la mission créée en DTO
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(MissionFullDTO.from(savedMission));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erreur lors de la création de la mission: " + e.getMessage());
        }
    }
}