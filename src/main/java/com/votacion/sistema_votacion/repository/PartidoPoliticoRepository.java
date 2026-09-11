package com.votacion.sistema_votacion.repository;

import com.votacion.sistema_votacion.model.PartidoPolitico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidoPoliticoRepository extends JpaRepository<PartidoPolitico, Long> {
}