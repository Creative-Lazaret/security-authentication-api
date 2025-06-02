package com.cdg.springjwt.repository;

import com.cdg.springjwt.models.DemandeAssignationMission;
import com.cdg.springjwt.models.Mission;
import com.cdg.springjwt.models.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemandeAssignationMissionRepository extends JpaRepository<DemandeAssignationMission, Long> {

    Optional<DemandeAssignationMission> findByMissionAndStatut(Mission mission, StatutDemande statut);

    boolean existsByMissionAndStatut(Mission mission, StatutDemande statut);
}
