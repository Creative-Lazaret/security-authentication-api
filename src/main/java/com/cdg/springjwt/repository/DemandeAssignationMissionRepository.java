package com.cdg.springjwt.repository;

import com.cdg.springjwt.models.DemandeAssignationMission;
import com.cdg.springjwt.models.Filiale;
import com.cdg.springjwt.models.Mission;
import com.cdg.springjwt.models.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DemandeAssignationMissionRepository extends JpaRepository<DemandeAssignationMission, Long>, JpaSpecificationExecutor<DemandeAssignationMission> {

    Optional<DemandeAssignationMission> findByMissionAndStatut(Mission mission, StatutDemande statut);

    boolean existsByMissionAndStatut(Mission mission, StatutDemande statut);

    // Méthodes optimisées avec filialeReceptrice
    List<DemandeAssignationMission> findByFilialeReceptriceAndStatut(Filiale filialeReceptrice, StatutDemande statut);

    @Query("SELECT d FROM DemandeAssignationMission d WHERE d.filialeDemandeuse = :filialeDemandeuse AND d.filialeReceptrice = :filialeReceptrice AND d.statut = :statut")
    List<DemandeAssignationMission> findByFilialeDemandeuseFilialeReceptriceAndStatut(
            @Param("filialeDemandeuse") Filiale filialeDemandeuse,
            @Param("filialeReceptrice") Filiale filialeReceptrice,
            @Param("statut") StatutDemande statut);

    @Query("SELECT d FROM DemandeAssignationMission d WHERE d.filialeReceptrice.id = :filialeId AND d.statut = :statut")
    List<DemandeAssignationMission> findByFilialeReceptriceIdAndStatut(@Param("filialeId") Long filialeId, @Param("statut") StatutDemande statut);

    // Nouvelle méthode pour compter
    @Query("SELECT COUNT(d) FROM DemandeAssignationMission d WHERE d.filialeReceptrice.id = :filialeId AND d.statut = :statut")
    long countByFilialeReceptriceIdAndStatut(@Param("filialeId") Long filialeId, @Param("statut") StatutDemande statut);

    @Query("SELECT d FROM DemandeAssignationMission d WHERE d.filialeDemandeuse.id = :filialeId AND d.statut = :statut")
    List<DemandeAssignationMission> findByFilialeDemandeuseeIdAndStatut(@Param("filialeId") Long filialeId, @Param("statut") StatutDemande statut);

    @Query("SELECT d FROM DemandeAssignationMission d WHERE d.filialeDemandeuse.id = :filialeDemandeuseeId AND d.filialeReceptrice.id = :filialeReceptriceId AND d.statut = :statut")
    List<DemandeAssignationMission> findByFilialeIds(
            @Param("filialeDemandeuseeId") Long filialeDemandeuseeId,
            @Param("filialeReceptriceId") Long filialeReceptriceId,
            @Param("statut") StatutDemande statut);
}