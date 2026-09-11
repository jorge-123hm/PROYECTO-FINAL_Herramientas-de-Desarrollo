package com.votacion.sistema_votacion.repository;

import com.votacion.sistema_votacion.model.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    Optional<Comprobante> findByCodigoVerificacion(String codigoVerificacion);
}
