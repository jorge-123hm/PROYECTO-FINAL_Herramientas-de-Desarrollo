package com.votacion.sistema_votacion.repository;

import com.votacion.sistema_votacion.model.Voto;
import com.votacion.sistema_votacion.model.Candidato;
import com.votacion.sistema_votacion.model.Eleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    long countByCandidatoAndEleccion(Candidato candidato, Eleccion eleccion);
    long countByEleccion(Eleccion eleccion);
}