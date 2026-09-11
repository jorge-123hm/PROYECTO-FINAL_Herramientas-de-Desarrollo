package com.votacion.sistema_votacion.repository;

import com.votacion.sistema_votacion.model.Eleccion;
import com.votacion.sistema_votacion.model.Participacion;
import com.votacion.sistema_votacion.model.Votante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, Long> {
    // Verifica si el votante ya participó en la elección
    Optional<Participacion> findByVotanteAndEleccion(Votante votante, Eleccion eleccion);
}