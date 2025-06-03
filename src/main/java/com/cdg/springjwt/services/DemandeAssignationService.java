package com.cdg.springjwt.services;

import com.cdg.springjwt.controllers.dto.CreateDemandeAssignationDTO;
import com.cdg.springjwt.models.*;
import com.cdg.springjwt.repository.CollaborateurRepository;
import com.cdg.springjwt.repository.DemandeAssignationMissionRepository;
import com.cdg.springjwt.repository.MissionRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandeAssignationService {

    private final DemandeAssignationMissionRepository demandeRepo;
    private final MissionRepository missionRepo;
    private final CollaborateurRepository collaborateurRepo;
    private final PdfGenerationService pdfService;
    private final MailService emailService;

    @Transactional
    public void creerDemande(CreateDemandeAssignationDTO dto, String usernameDemandeur) throws MessagingException {
        log.info("Creating demande assignation for mission: {} by user: {}", dto.getMissionId(), usernameDemandeur);

        try {
            Mission mission = missionRepo.findById(dto.getMissionId())
                    .orElseThrow(() -> {
                        log.error("Mission not found with ID: {}", dto.getMissionId());
                        return new RuntimeException("Mission introuvable");
                    });

            Collaborateur collaborateur = collaborateurRepo.findCollaborateurByColabMatricule(dto.getCollaborateurMatricule())
                    .orElseThrow(() -> {
                        log.error("Collaborateur not found with matricule: {}", dto.getCollaborateurMatricule());
                        return new RuntimeException("Collaborateur introuvable");
                    });

            boolean existe = demandeRepo.existsByMissionAndStatut(mission, StatutDemande.EN_ATTENTE);
            if (existe) {
                log.warn("Request already exists for mission: {} with status EN_ATTENTE", mission.getCode());
                throw new IllegalStateException("Une demande est déjà en cours pour cette mission.");
            }

            DemandeAssignationMission demande = DemandeAssignationMission.builder()
                    .mission(mission)
                    .collaborateur(collaborateur)
                    .filialeDemandeuse(mission.getFiliale())
                    .statut(StatutDemande.EN_ATTENTE)
                    .dateCreation(LocalDateTime.now())
                    .creePar(usernameDemandeur)
                    .build();

            demandeRepo.save(demande);
            log.info("Demande assignation saved with ID: {}", demande.getId());

            mission.setStatut(EStatutMission.EN_ATTENTE_APPROBABTION);
            missionRepo.save(mission);
            log.info("Mission status updated to EN_ATTENTE_APPROBABTION for mission: {}", mission.getCode());

            // Générer PDF et envoyer par email
            try {
                byte[] pdf = pdfService.generateDemandePdf(mission, collaborateur);
                emailService.envoyerDemandeAvecPdf(collaborateur.getContactRh(), pdf, collaborateur.getColabMatricule() + ".pdf");
                log.info("PDF generated and email sent successfully for demande: {}", demande.getId());
            } catch (Exception e) {
                log.error("Error generating PDF or sending email for demande: {}. Error: {}", demande.getId(), e.getMessage(), e);
                throw new RuntimeException("Erreur lors de la génération du PDF ou l'envoi de l'email", e);
            }

        } catch (IllegalStateException e) {
            log.warn("Business rule violation while creating demande: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Runtime error while creating demande for mission: {}. Error: {}", dto.getMissionId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating demande for mission: {}. Error: {}", dto.getMissionId(), e.getMessage(), e);
            throw new RuntimeException("Erreur inattendue lors de la création de la demande", e);
        }
    }

    @Transactional
    public void accepterDemande(Long demandeId, String usernameRh) {
        log.info("Accepting demande with ID: {} by RH user: {}", demandeId, usernameRh);

        try {
            DemandeAssignationMission demande = demandeRepo.findById(demandeId)
                    .orElseThrow(() -> {
                        log.error("Demande not found with ID: {}", demandeId);
                        return new IllegalArgumentException("Demande non trouvée");
                    });

            if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
                log.warn("Attempt to modify demande {} with status: {}", demandeId, demande.getStatut());
                throw new IllegalStateException("La demande n'est plus modifiable.");
            }

            Mission mission = demande.getMission();
            Collaborateur collaborateur = demande.getCollaborateur();

            // Mise à jour de la demande
            demande.setStatut(StatutDemande.ACCEPTEE);
            demande.setDateDecision(LocalDateTime.now());

            // Mise à jour de la mission
            mission.getRessources().add(collaborateur);
            mission.setStatut(EStatutMission.EN_COURS);

            // Mise à jour de la disponibilité du collaborateur
            collaborateur.setDisponible(false);

            // Sauvegarde
            demandeRepo.save(demande);
            missionRepo.save(mission);
            collaborateurRepo.save(collaborateur);

            log.info("Demande {} accepted successfully. Mission: {}, Collaborateur: {}", 
                    demandeId, mission.getCode(), collaborateur.getColabMatricule());

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Business rule violation while accepting demande {}: {}", demandeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while accepting demande {}: {}", demandeId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'acceptation de la demande", e);
        }
    }

    @Transactional
    public void refuserDemande(Long demandeId, String usernameRh, String commentaire) {
        log.info("Refusing demande with ID: {} by RH user: {} with comment: {}", demandeId, usernameRh, commentaire);

        try {
            DemandeAssignationMission demande = demandeRepo.findById(demandeId)
                    .orElseThrow(() -> {
                        log.error("Demande not found with ID: {}", demandeId);
                        return new IllegalArgumentException("Demande non trouvée");
                    });

            if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
                log.warn("Attempt to modify demande {} with status: {}", demandeId, demande.getStatut());
                throw new IllegalStateException("La demande n'est plus modifiable.");
            }

            // Mise à jour de la demande
            demande.setStatut(StatutDemande.REFUSEE);
            demande.setDateDecision(LocalDateTime.now());
            demande.setCommentaire(commentaire);

            // Repasser la mission en "OUVERTE" si elle était en attente
            Mission mission = demande.getMission();
            if (mission.getStatut() == EStatutMission.EN_ATTENTE_APPROBABTION) {
                mission.setStatut(EStatutMission.OUVERTE);
                missionRepo.save(mission);
                log.info("Mission status reverted to OUVERTE for mission: {}", mission.getCode());
            }

            // Sauvegarde
            demandeRepo.save(demande);

            log.info("Demande {} refused successfully. Mission: {}", demandeId, mission.getCode());

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Business rule violation while refusing demande {}: {}", demandeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while refusing demande {}: {}", demandeId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors du refus de la demande", e);
        }
    }
}