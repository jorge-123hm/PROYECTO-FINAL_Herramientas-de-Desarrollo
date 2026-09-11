package com.votacion.sistema_votacion.repository;

import com.votacion.sistema_votacion.model.Otp;
import com.votacion.sistema_votacion.model.Votante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByVotanteAndUsadoFalse(Votante votante);
    List<Otp> findAllByVotanteAndUsadoFalse(Votante votante);
}