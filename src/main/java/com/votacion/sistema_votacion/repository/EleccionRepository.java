package com.votacion.sistema_votacion.repository;

import com.votacion.sistema_votacion.model.Eleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EleccionRepository extends JpaRepository<Eleccion, Long> {
    List<Eleccion> findByEstado(String estado);
    List<Eleccion> findByPublicadaTrue();
}