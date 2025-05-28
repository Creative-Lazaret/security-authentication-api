package com.cdg.springjwt.repository;

import com.cdg.springjwt.models.Collaborateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long>, JpaSpecificationExecutor<Collaborateur> {

    @EntityGraph(attributePaths = {"filiale"})
    Page<Collaborateur> findAll(Specification<Collaborateur> spec, Pageable pageable);

    // Alternative avec pagination
//    Page<Collaborateur> findByFiliale_Id(String filialeId, Pageable pageable);

    Page<Collaborateur> findByFiliale_IdIn(List<Long> filialeIds, Pageable pageable);

    Optional<Collaborateur> findCollaborateurByColabMatricule(String matricule);

//    @Query("SELECT c FROM Collaborateur c WHERE c.filiale.id = :filialeId")
//    Page<Collaborateur> findAllByFilialeId(@Param("filialeId") String filialeId, Pageable pageable);
}
