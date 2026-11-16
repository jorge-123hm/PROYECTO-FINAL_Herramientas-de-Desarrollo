package com.votacion.sistema_votacion.config;

import com.votacion.sistema_votacion.model.Administrador;
import com.votacion.sistema_votacion.model.Candidato;
import com.votacion.sistema_votacion.model.Eleccion;
import com.votacion.sistema_votacion.model.PartidoPolitico;
import com.votacion.sistema_votacion.model.Votante;
import com.votacion.sistema_votacion.repository.AdministradorRepository;
import com.votacion.sistema_votacion.repository.CandidatoRepository;
import com.votacion.sistema_votacion.repository.EleccionRepository;
import com.votacion.sistema_votacion.repository.PartidoPoliticoRepository;
import com.votacion.sistema_votacion.repository.VotanteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Carga datos de prueba al arrancar. Solo se activa con app.seed.enabled=true
 * (por ejemplo, dentro de los contenedores Docker).
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AdministradorRepository administradorRepository;
    private final VotanteRepository votanteRepository;
    private final PartidoPoliticoRepository partidoRepository;
    private final EleccionRepository eleccionRepository;
    private final CandidatoRepository candidatoRepository;

    public DataSeeder(AdministradorRepository administradorRepository,
            VotanteRepository votanteRepository,
            PartidoPoliticoRepository partidoRepository,
            EleccionRepository eleccionRepository,
            CandidatoRepository candidatoRepository) {
        this.administradorRepository = administradorRepository;
        this.votanteRepository = votanteRepository;
        this.partidoRepository = partidoRepository;
        this.eleccionRepository = eleccionRepository;
        this.candidatoRepository = candidatoRepository;
    }

    @Override
    public void run(String... args) {
        // Idempotente: si ya existe el admin de prueba, no vuelve a cargar nada
        if (administradorRepository.findByUsuario("admin").isPresent()) {
            log.info("Datos de prueba ya existentes, se omite el seeder");
            return;
        }

        administradorRepository.save(
                new Administrador("admin", "admin123", "Administrador de Prueba"));

        votanteRepository.save(new Votante("12345678", "Juan Carlos", "Perez Rojas", "987654321"));
        votanteRepository.save(new Votante("23456789", "Maria Elena", "Gomez Diaz", "976543210"));
        votanteRepository.save(new Votante("34567890", "Luis Alberto", "Torres Vega", "965432109"));
        votanteRepository.save(new Votante("45678901", "Ana Lucia", "Ramos Soto", "954321098"));
        votanteRepository.save(new Votante("56789012", "Pedro Pablo", "Castro Luna", "943210987"));

        PartidoPolitico partidoA = partidoRepository.save(new PartidoPolitico("Partido Futuro", null));
        PartidoPolitico partidoB = partidoRepository.save(new PartidoPolitico("Alianza Ciudadana", null));

        LocalDateTime ahora = LocalDateTime.now();
        Eleccion eleccion = eleccionRepository.save(new Eleccion(
                "Elecciones Estudiantiles 2026",
                "Eleccion de prueba para validar el sistema en contenedores",
                ahora.minusDays(1),
                ahora.plusDays(30),
                "ACTIVA"));

        candidatoRepository.save(new Candidato("Carla", "Mendoza Rios", null, partidoA, eleccion));
        candidatoRepository.save(new Candidato("Diego", "Salazar Pinto", null, partidoA, eleccion));
        candidatoRepository.save(new Candidato("Rosa", "Herrera Cano", null, partidoB, eleccion));
        candidatoRepository.save(new Candidato("Marcos", "Quispe Flores", null, partidoB, eleccion));

        log.info("Datos de prueba cargados: 1 admin, 5 votantes, 2 partidos, 1 eleccion y 4 candidatos");
    }
}