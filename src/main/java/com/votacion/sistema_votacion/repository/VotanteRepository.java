package com.votacion.sistema_votacion.repository;

import com.votacion.sistema_votacion.model.Votante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VotanteRepository extends JpaRepository<Votante, Long> {
    Optional<Votante> findByDni(String dni);
}