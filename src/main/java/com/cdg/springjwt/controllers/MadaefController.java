package com.cdg.springjwt.controllers;

import com.cdg.springjwt.controllers.dto.CollaborateurDTO;
import com.cdg.springjwt.models.Collaborateur;
import com.cdg.springjwt.models.EFiliale;
import com.cdg.springjwt.models.Filiale;
import com.cdg.springjwt.models.User;
import com.cdg.springjwt.repository.CollaborateurRepository;
import com.cdg.springjwt.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/madaef")
public class MadaefController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    CollaborateurRepository collaborateurRepository;


    @GetMapping("/myCollaborateurs")
    @PreAuthorize("hasRole('USER')")
    public Page<CollaborateurDTO> getCollaborateurs(@RequestParam(required = false) String nomPrenom,
                                                    @RequestParam(required = false) String domaine,
                                                    @RequestParam(required = false) String metier,
                                                    @RequestParam(required = false) String typeDisponibilite,
                                                    @RequestParam(required = false) Boolean disponible,
                                                    @PageableDefault(size = 10) Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("ERROR: user not found"));
        List<Long> filiales = user.getFiliales().stream().map(Filiale::getId).collect(Collectors.toList());

        return collaborateurRepository.findAll((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    //retrieve only collabs that belongs to user's filiales
                    predicates.add(root.get("filiale").get("id").in(filiales));

                    if (nomPrenom != null && !nomPrenom.isBlank()) {
                        String pattern = "%" + nomPrenom.toLowerCase() + "%";
                        Predicate nom = cb.like(cb.lower(root.get("colabNom")), pattern);
                        Predicate prenom = cb.like(cb.lower(root.get("colabPrenom")), pattern);
                        predicates.add(cb.or(nom, prenom));
                    }


                    if (domaine != null && !domaine.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("domaine")), domaine.toLowerCase()));
                    }

                    if (metier != null && !metier.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("metier")), metier.toLowerCase()));
                    }

                    if (typeDisponibilite != null && !typeDisponibilite.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("typeDisponibilite")), typeDisponibilite.toLowerCase()));
                    }

                    if (disponible != null) {
                        predicates.add(cb.equal(root.get("disponible"), disponible));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }, pageable)
                .map(CollaborateurDTO::from);


    }

    @GetMapping("/searchCollaborateurs")
    @PreAuthorize("hasRole('USER')")
    public Page<CollaborateurDTO> searchCollaborateurs(
            @RequestParam(required = false) String nomPrenom,
            @RequestParam(required = false) String filiale,
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) String metier,
            @RequestParam(required = false) String typeDisponibilite,
            @RequestParam(required = false) Boolean disponible,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return collaborateurRepository.findAll((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (nomPrenom != null && !nomPrenom.isBlank()) {
                        String pattern = "%" + nomPrenom.toLowerCase() + "%";
                        Predicate nom = cb.like(cb.lower(root.get("colabNom")), pattern);
                        Predicate prenom = cb.like(cb.lower(root.get("colabPrenom")), pattern);
                        predicates.add(cb.or(nom, prenom));
                    }

                    if (filiale != null && !filiale.isBlank()) {
                        try {
                            EFiliale eFiliale = EFiliale.valueOf(filiale);
                            predicates.add(cb.equal(root.get("filiale").get("name"), eFiliale));
                        } catch (IllegalArgumentException ex) {
                            // filiale inconnue, ne pas filtrer
                            System.out.println("⚠️ Filiale inconnue : " + filiale);
                        }
                    }


                    if (domaine != null && !domaine.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("domaine")), domaine.toLowerCase()));
                    }

                    if (metier != null && !metier.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("metier")), metier.toLowerCase()));
                    }

                    if (typeDisponibilite != null && !typeDisponibilite.isBlank()) {
                        predicates.add(cb.equal(cb.lower(root.get("typeDisponibilite")), typeDisponibilite.toLowerCase()));
                    }

                    if (disponible != null) {
                        predicates.add(cb.equal(root.get("disponible"), disponible));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }, pageable)
                .map(CollaborateurDTO::from);
    }

    @PutMapping("/myCollaborateurs/{matricule}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCollaborateur(@PathVariable String matricule,
                                                 @RequestBody Collaborateur updated) {
        Optional<Collaborateur> optional = collaborateurRepository.findCollaborateurByColabMatricule(matricule);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        Collaborateur existing = optional.get();

        existing.setColabNom(updated.getColabNom());
        existing.setColabPrenom(updated.getColabPrenom());
        existing.setDomaine(updated.getDomaine());
        existing.setMetier(updated.getMetier());
        existing.setMaitrise(updated.getMaitrise());
        existing.setTypeDisponibilite(updated.getTypeDisponibilite());
        existing.setDisponible(updated.getDisponible());
        existing.setPeriodeDisponibilite(updated.getPeriodeDisponibilite());
//        existing.setNombreMission(updated.getNombreMission());
        existing.setRating(updated.getRating());
        existing.setCv(updated.getCv());
        existing.setDateEntreeFiliale(updated.getDateEntreeFiliale());
        existing.setDateEntreeGroup(updated.getDateEntreeGroup());

        collaborateurRepository.save(existing);
        return ResponseEntity.ok().build();
    }


}
