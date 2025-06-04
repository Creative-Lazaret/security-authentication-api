package com.cdg.springjwt.controllers;

import com.cdg.springjwt.controllers.dto.CreateDemandeAssignationDTO;
import com.cdg.springjwt.controllers.dto.DemandeAssignationDTO;
import com.cdg.springjwt.controllers.dto.RefusDemandeeDTO;
import com.cdg.springjwt.security.services.UserDetailsImpl;
import com.cdg.springjwt.services.DemandeAssignationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/demande-assignation")
@RequiredArgsConstructor
public class DemandeAssignationController {

    private final DemandeAssignationService demandeAssignationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> creerDemande(@RequestBody CreateDemandeAssignationDTO dto,
                                          @AuthenticationPrincipal UserDetailsImpl user) {
        try {
            demandeAssignationService.creerDemande(dto, user.getUsername());
            return ResponseEntity.ok("Demande créée et envoyée au RH.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while creating demande", e);
            return ResponseEntity.internalServerError().body("Erreur lors de la création de la demande.");
        }
    }

    @GetMapping("/recues")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<DemandeAssignationDTO>> getDemandesRecues(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String filialeDemandeuse,
            @RequestParam(required = false) String metier,
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) String collaborateurMatricule,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserDetailsImpl user) {

        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

            Page<DemandeAssignationDTO> demandes = demandeAssignationService.getDemandesRecues(
                    user.getFilialesId(), statut, filialeDemandeuse, metier, domaine, collaborateurMatricule, pageable);

            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            log.error("Error while fetching demandes recues", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/emises")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<DemandeAssignationDTO>> getDemandesEmises(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String filialeReceptrice,
            @RequestParam(required = false) String metier,
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) String collaborateurMatricule,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserDetailsImpl user) {

        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

            Page<DemandeAssignationDTO> demandes = demandeAssignationService.getDemandesEmises(
                    user.getFilialesId(), statut, filialeReceptrice, metier, domaine, collaborateurMatricule, pageable);

            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            log.error("Error while fetching demandes emises", e);
            return ResponseEntity.internalServerError().build();
        }
    }

//    @GetMapping("/recues/count")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<Map<String, Long>> getDemandesRecuesCount(
//            @AuthenticationPrincipal UserDetailsImpl user) {
//        try {
//            Map<String, Long> counts = demandeAssignationService.getDemandesRecuesCount(user.getFilialesId());
//            return ResponseEntity.ok(counts);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }

    @PostMapping("/{demandeId}/accepter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> accepterDemande(
            @PathVariable Long demandeId,
            @AuthenticationPrincipal UserDetailsImpl user) {
        try {
            demandeAssignationService.accepterDemande(demandeId, user.getUsername());
            return ResponseEntity.ok("Demande acceptée avec succès.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while accepting demande {}", demandeId, e);
            return ResponseEntity.internalServerError().body("Erreur lors de l'acceptation de la demande.");
        }
    }

    @PostMapping("/{demandeId}/refuser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> refuserDemande(
            @PathVariable Long demandeId,
            @RequestBody RefusDemandeeDTO refusDto,
            @AuthenticationPrincipal UserDetailsImpl user) {
        try {
            demandeAssignationService.refuserDemande(demandeId, user.getUsername(), refusDto.getCommentaire());
            return ResponseEntity.ok("Demande refusée avec succès.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while refusing demande {}", demandeId, e);
            return ResponseEntity.internalServerError().body("Erreur lors du refus de la demande.");
        }
    }
}