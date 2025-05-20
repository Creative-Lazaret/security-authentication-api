package com.cdg.springjwt.repository;

import com.cdg.springjwt.models.Collaborateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, String> {

    // Alternative avec pagination
//    Page<Collaborateur> findByFiliale_Id(String filialeId, Pageable pageable);

    Page<Collaborateur> findByFiliale_IdIn(List<Long> filialeIds, Pageable pageable);

//    @Query("SELECT c FROM Collaborateur c WHERE c.filiale.id = :filialeId")
//    Page<Collaborateur> findAllByFilialeId(@Param("filialeId") String filialeId, Pageable pageable);
}
