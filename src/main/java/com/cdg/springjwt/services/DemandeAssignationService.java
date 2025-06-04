package com.cdg.springjwt.services;

import com.cdg.springjwt.controllers.dto.CreateDemandeAssignationDTO;
import com.cdg.springjwt.controllers.dto.DemandeAssignationDTO;
import com.cdg.springjwt.models.*;
import com.cdg.springjwt.repository.CollaborateurRepository;
import com.cdg.springjwt.repository.DemandeAssignationMissionRepository;
import com.cdg.springjwt.repository.MissionRepository;
import com.cdg.springjwt.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandeAssignationService {

    private final DemandeAssignationMissionRepository demandeRepo;
    private final MissionRepository missionRepo;
    private final UserRepository userRepo;
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

            if (mission.getFiliale() == collaborateur.getFiliale()) {
                log.error("Attempt to assign a collaborateur to a mission from its own filiale");
                throw new IllegalStateException("Une demande ne peut être assignée à un collaborateur de sa filiale.");
            }
            boolean existe = demandeRepo.existsByMissionAndStatut(mission, StatutDemande.EN_ATTENTE);
            if (existe) {
                log.warn("Request already exists for mission: {} with status EN_ATTENTE", mission.getCode());
                throw new IllegalStateException("Une demande est déjà en cours pour cette mission.");
            }

            Optional<User> optionalUser = userRepo.findByUsername(usernameDemandeur);
            if (optionalUser.isEmpty()) {
                log.error("User not found with username: {}", usernameDemandeur);
                throw new RuntimeException("L'utilisateur n'existe pas.");

            }


            User userDemandeur = optionalUser.get();
            DemandeAssignationMission demande = DemandeAssignationMission.builder()
                    .mission(mission)
                    .collaborateur(collaborateur)
                    .filialeDemandeuse(mission.getFiliale())
                    .filialeReceptrice(collaborateur.getFiliale())
                    .statut(StatutDemande.EN_ATTENTE)
                    .dateCreation(LocalDateTime.now())
                    .creePar(userDemandeur.getNom() + " " + userDemandeur.getPrenom())
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
    // Ajoutez ces méthodes à DemandeAssignationService

    @Transactional(readOnly = true)
    public Page<DemandeAssignationDTO> getDemandesRecues(
            List<Long> filialesIds,
            String statut,
            String filialeDemandeuse,
            String metier,
            String domaine,
            String collaborateurMatricule,
            Pageable pageable) {

        log.info("Fetching demandes reçues for filiales: {} with filters", filialesIds);

        Specification<DemandeAssignationMission> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtre principal : demandes reçues par les filiales de l'utilisateur
            predicates.add(root.get("filialeReceptrice").get("id").in(filialesIds));

            // Filtres optionnels
            if (statut != null && !statut.isBlank()) {
                try {
                    StatutDemande statutEnum = StatutDemande.valueOf(statut.toUpperCase());
                    predicates.add(cb.equal(root.get("statut"), statutEnum));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter: {}", statut);
                }
            }

            if (filialeDemandeuse != null && !filialeDemandeuse.isBlank()) {
                try {
                    EFiliale filiale = EFiliale.valueOf(filialeDemandeuse.toUpperCase());
                    predicates.add(cb.equal(root.get("filialeDemandeuse").get("name"), filiale));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid filiale filter: {}", filialeDemandeuse);
                }
            }

            if (metier != null && !metier.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("mission").get("metier")),
                        "%" + metier.toLowerCase() + "%"));
            }

            if (domaine != null && !domaine.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("mission").get("domaine")),
                        "%" + domaine.toLowerCase() + "%"));
            }

            if (collaborateurMatricule != null && !collaborateurMatricule.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("collaborateur").get("colabMatricule")),
                        "%" + collaborateurMatricule.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return demandeRepo.findAll(spec, pageable).map(DemandeAssignationDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<DemandeAssignationDTO> getDemandesEmises(
            List<Long> filialesIds,
            String statut,
            String filialeReceptrice,
            String metier,
            String domaine,
            String collaborateurMatricule,
            Pageable pageable) {

        log.info("Getting demandes emises for filiales: {} with filters - statut: {}, filialeReceptrice: {}, metier: {}, domaine: {}, collaborateur: {}",
                filialesIds, statut, filialeReceptrice, metier, domaine, collaborateurMatricule);

        // Utilisation de Specification pour créer une requête dynamique
        Specification<DemandeAssignationMission> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtre par filiales demandeuses (où l'utilisateur émet les demandes)
            if (filialesIds != null && !filialesIds.isEmpty()) {
                predicates.add(root.get("filialeDemandeuse").get("id").in(filialesIds));
            }

            // Filtre par statut
            if (statut != null && !statut.trim().isEmpty()) {
                try {
                    StatutDemande statutEnum = StatutDemande.valueOf(statut.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("statut"), statutEnum));
                } catch (IllegalArgumentException e) {
                    log.warn("Statut invalide: {}", statut);
                }
            }

            // Filtre par filiale réceptrice
            if (filialeReceptrice != null && !filialeReceptrice.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("filialeReceptrice").get("name")),
                        "%" + filialeReceptrice.toLowerCase() + "%"
                ));
            }

            // Filtre par métier
            if (metier != null && !metier.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("mission").get("metier")),
                        "%" + metier.toLowerCase() + "%"
                ));
            }

            // Filtre par domaine
            if (domaine != null && !domaine.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("mission").get("domaine")),
                        "%" + domaine.toLowerCase() + "%"
                ));
            }

            // Filtre par matricule collaborateur
            if (collaborateurMatricule != null && !collaborateurMatricule.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("mission").get("collaborateur").get("matricule")),
                        "%" + collaborateurMatricule.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return demandeRepo.findAll(spec, pageable).map(this::mapToDTO);
    }


    private DemandeAssignationDTO mapToDTO(DemandeAssignationMission demande) {

        DemandeAssignationDTO dto = DemandeAssignationDTO.builder().build();
        dto.setId(demande.getId());
        dto.setStatut(demande.getStatut());
        dto.setDateCreation(demande.getDateCreation());
        dto.setDateDecision(demande.getDateDecision());
        dto.setCommentaire(demande.getCommentaire());
        dto.setCreePar(demande.getCreePar());

        dto.setCollaborateurNom(demande.getCollaborateur().getColabNom());
        dto.setCollaborateurPrenom(demande.getCollaborateur().getColabPrenom());
        dto.setCollaborateurMatricule(demande.getCollaborateur().getColabMatricule());
        dto.setCollaborateurMetier(demande.getMission().getMetier());

        // Mapping mission
        if (demande.getMission() != null) {
            dto.setMissionCode(demande.getMission().getCode());
            dto.setMissionId(demande.getMission().getId());
            dto.setMissionTitre(demande.getMission().getTitre());
            dto.setMissionDescription(demande.getMission().getDescription());
            dto.setMissionMetier(demande.getMission().getMetier());
            dto.setMissionDomaine(demande.getMission().getDomaine());

            // Mapping collaborateur
            if (demande.getMission().getRessources() != null && !demande.getMission().getRessources().isEmpty()) {
                demande.getMission().getRessources().forEach(c -> {
                    dto.setCollaborateurNom(c.getColabNom());
                    dto.setCollaborateurPrenom(c.getColabPrenom());
                    dto.setCollaborateurMatricule(c.getColabMatricule());
                });

            }
        }

        // Mapping filiale demandeuse
        if (demande.getFilialeDemandeuse() != null) {
            dto.setFilialeDemandeuse(demande.getFilialeDemandeuse().getName().name());
        }

        // Mapping filiale réceptrice
        if (demande.getFilialeReceptrice() != null) {
            dto.setFilialeReceptrice(demande.getFilialeReceptrice().getName().name());
        }

        return dto;
    }
//    @Transactional(readOnly = true)
//    public Map<String, Long> getDemandesRecuesCount(List<Long> filialesIds) {
//        log.info("Getting demandes count for filiales: {}", filialesIds);
//
//        Map<String, Long> counts = new HashMap<>();
//
//        for (Long filialeId : filialesIds) {
//            long enAttente = demandeRepo.countByFilialeReceptriceIdAndStatut(filialeId, StatutDemande.EN_ATTENTE);
//            long acceptees = demandeRepo.countByFilialeReceptriceIdAndStatut(filialeId, StatutDemande.ACCEPTEE);
//            long refusees = demandeRepo.countByFilialeReceptriceIdAndStatut(filialeId, StatutDemande.REFUSEE);
//
//            counts.put("EN_ATTENTE_" + filialeId, enAttente);
//            counts.put("ACCEPTEE_" + filialeId, acceptees);
//            counts.put("REFUSEE_" + filialeId, refusees);
//        }
//
//        // Totaux globaux
//        long totalEnAttente = filialesIds.stream()
//                .mapToLong(id -> demandeRepo.countByFilialeReceptriceIdAndStatut(id, StatutDemande.EN_ATTENTE))
//                .sum();
//
//        counts.put("TOTAL_EN_ATTENTE", totalEnAttente);
//        counts.put("TOTAL_ACCEPTEE", filialesIds.stream()
//                .mapToLong(id -> demandeRepo.countByFilialeReceptriceIdAndStatut(id, StatutDemande.ACCEPTEE))
//                .sum());
//        counts.put("TOTAL_REFUSEE", filialesIds.stream()
//                .mapToLong(id -> demandeRepo.countByFilialeReceptriceIdAndStatut(id, StatutDemande.REFUSEE))
//                .sum());
//
//        return counts;
//    }
}