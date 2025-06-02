package com.cdg.springjwt.services;

import com.cdg.springjwt.controllers.dto.CreateDemandeAssignationDTO;
import com.cdg.springjwt.models.*;
import com.cdg.springjwt.repository.CollaborateurRepository;
import com.cdg.springjwt.repository.DemandeAssignationMissionRepository;
import com.cdg.springjwt.repository.MissionRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DemandeAssignationService {

    private final DemandeAssignationMissionRepository demandeRepo;
    private final MissionRepository missionRepo;
    private final CollaborateurRepository collaborateurRepo;
    private final PdfGenerationService pdfService;
    private final MailService emailService;

    @Transactional
    public void creerDemande(CreateDemandeAssignationDTO dto, String usernameDemandeur) throws MessagingException {
        Mission mission = missionRepo.findById(dto.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission introuvable"));

        Collaborateur collaborateur = collaborateurRepo.findCollaborateurByColabMatricule(dto.getCollaborateurMatricule())
                .orElseThrow(() -> new RuntimeException("Collaborateur introuvable"));

        boolean existe = demandeRepo.existsByMissionAndStatut(mission, StatutDemande.EN_ATTENTE);
        if (existe) {
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

        mission.setStatut(EStatutMission.EN_ATTENTE_APPROBABTION);
        missionRepo.save(mission);

//         Générer PDF et envoyer par email
        byte[] pdf = pdfService.generateDemandePdf(mission,collaborateur);
        emailService.envoyerDemandeAvecPdf(collaborateur.getContactRh(), pdf, collaborateur.getColabMatricule()+".pdf");
    }


    @Transactional
    public void accepterDemande(Long demandeId, String usernameRh) {
        DemandeAssignationMission demande = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("La demande n’est plus modifiable.");
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
    }

    @Transactional
    public void refuserDemande(Long demandeId, String usernameRh, String commentaire) {
        DemandeAssignationMission demande = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("La demande n’est plus modifiable.");
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
        }

        // Sauvegarde
        demandeRepo.save(demande);
    }
}
