package com.cdg.springjwt.controllers;

import com.cdg.springjwt.controllers.dto.CreateDemandeAssignationDTO;
import com.cdg.springjwt.security.services.UserDetailsImpl;
import com.cdg.springjwt.services.DemandeAssignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.internalServerError().body("Erreur lors de la création de la demande.");
        }
    }
}
