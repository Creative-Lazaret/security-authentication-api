package com.cdg.springjwt.repository;

import com.cdg.springjwt.models.EFiliale;
import com.cdg.springjwt.models.Filiale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilialeRepository extends JpaRepository<Filiale, Long> {

    Optional<Filiale> findByName(EFiliale name);
}
