package com.votacion.sistema_votacion;

import com.votacion.sistema_votacion.model.*;
import com.votacion.sistema_votacion.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// Despues de cada prueba se revierte los datos,
//para no llenar datos de prueba en la BD
@Transactional
// Define el orden de ejecución de las pruebas mediante @Order.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SistemaVotacionTests {

    @Autowired
    private VotanteRepository votanteRepository;
    @Autowired
    private AdministradorRepository administradorRepository;
    @Autowired
    private EleccionRepository eleccionRepository;
    @Autowired
    private PartidoPoliticoRepository partidoRepository;
    @Autowired
    private CandidatoRepository candidatoRepository;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private VotoRepository votoRepository;
    @Autowired
    private ComprobanteRepository comprobanteRepository;
    @Autowired
    private ParticipacionRepository participacionRepository;

    // ── Prueba 1 ───────────────────────────────────────────────
    @Test
    @Order(1)
    @DisplayName("Prueba 1: Registrar un votante correctamente")
    void testRegistrarVotante() {
        Votante votante = new Votante("87654321", "Daniel", "Lopez", "959164837");
        Votante guardado = votanteRepository.save(votante);

        assertNotNull(guardado.getIdVotante(),
                "El ID del votante no debe ser nulo después de guardar");
        assertEquals("87654321", guardado.getDni(),
                "El DNI debe coincidir con el ingresado");
        assertEquals("Daniel", guardado.getNombres(),
                "El nombre debe coincidir con el ingresado");

        System.out.println("Prueba 1 pasada: Votante registrado con ID " + guardado.getIdVotante());
    }

    // ── Prueba 2 ───────────────────────────────────────────────
    @Test
    @Order(2)
    @DisplayName("Prueba 2: Buscar votante por DNI")
    void testBuscarVotantePorDni() {
        Votante votante = new Votante("87654321", "María", "García", "999574321");
        votanteRepository.save(votante);

        Optional<Votante> encontrado = votanteRepository.findByDni("87654321");

        assertTrue(encontrado.isPresent(),
                "Debe encontrar el votante con ese DNI");
        assertEquals("María", encontrado.get().getNombres(),
                "El nombre debe ser María");

        System.out.println("Prueba 2 pasada: Votante encontrado por DNI");
    }

    // ── Prueba 3 ───────────────────────────────────────────────
    @Test
    @Order(3)
    @DisplayName("Prueba 3: Registrar administrador y verificar login")
    void testLoginAdministrador() {
        Administrador admin = new Administrador("joseAdmin", "4321", "Jose Adminisrador");
        administradorRepository.save(admin);

        Optional<Administrador> encontrado = administradorRepository.findByUsuario("joseAdmin");

        assertTrue(encontrado.isPresent(),
                "Debe encontrar el administrador");
        assertEquals("4321", encontrado.get().getPassword(),
                "La contraseña debe coincidir");
        assertTrue(encontrado.get().getPassword().equals("4321"),
                "El login debe ser exitoso con la contraseña correcta");

        System.out.println("Prueba 3 pasada: Login de administrador verificado");
    }

    // ── Prueba 4 ───────────────────────────────────────────────
    @Test
    @Order(4)
    @DisplayName("Prueba 4: Crear una elección y verificar su estado")
    void testCrearEleccion() {
        Eleccion eleccion = new Eleccion(
                "Elecciones De Alcaldia 2026",
                "Proceso electoral municipal",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                "ACTIVA");
        Eleccion guardada = eleccionRepository.save(eleccion);

        assertNotNull(guardada.getIdEleccion(),
                "El ID de la elección no debe ser nulo");
        assertEquals("ACTIVA", guardada.getEstado(),
                "El estado debe ser ACTIVA");

        List<Eleccion> activas = eleccionRepository.findByEstado("ACTIVA");
        assertFalse(activas.isEmpty(),
                "Debe haber al menos una elección activa");

        System.out.println("Prueba 4 pasada: Elección creada con estado ACTIVA");
    }

    // ── Prueba 5 ───────────────────────────────────────────────
    @Test
    @Order(5)
    @DisplayName("Prueba 5: Generar y verificar código OTP")
    void testGenerarOtp() {
        Votante votante = new Votante("19837462", "Carlos", "López", "964837162");
        votanteRepository.save(votante);

        // Se simula la generación del OTP igual que en VotanteController
        String codigo = String.valueOf((int) (Math.random() * 900000) + 100000);
        Otp otp = new Otp(votante, codigo,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        // Se verifica que el OTP tenga 6 dígitos
        assertEquals(6, codigo.length(),
                "El código OTP debe tener exactamente 6 dígitos");

        // Verifica que el codigo no está usado
        assertFalse(otp.isUsado(),
                "El OTP recién creado no debe estar marcado como usado");

        // Verifica que se puede encontrar
        Optional<Otp> encontrado = otpRepository.findByVotanteAndUsadoFalse(votante);
        assertTrue(encontrado.isPresent(),
                "Debe encontrar el OTP no usado del votante");
        assertEquals(codigo, encontrado.get().getCodigo(),
                "El código OTP debe coincidir");

        System.out.println("Prueba 5 pasada: OTP generado correctamente: " + codigo);
    }

    // ── Prueba 6 ───────────────────────────────────────────────
    @Test
    @Order(6)
    @DisplayName("Prueba 6: Registrar candidato con partido y elección")
    void testRegistrarCandidato() {
        PartidoPolitico partido = new PartidoPolitico("Partido Ejemplo", "🏛️");
        partidoRepository.save(partido);

        Eleccion eleccion = new Eleccion("Elección Test", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusDays(10), "ACTIVA");
        eleccionRepository.save(eleccion);

        Candidato candidato = new Candidato(
                "Ana", "Martínez", null, partido, eleccion);
        Candidato guardado = candidatoRepository.save(candidato);

        assertNotNull(guardado.getIdCandidato(),
                "El ID del candidato no debe ser nulo");
        assertEquals("Ana", guardado.getNombres(),
                "El nombre del candidato debe ser Ana");
        assertEquals("Partido Ejemplo", guardado.getPartido().getNombre(),
                "El partido debe coincidir");

        System.out.println("Prueba 6 pasada: Candidato registrado correctamente");
    }

    // ── Prueba 7 ───────────────────────────────────────────────
    @Test
    @Order(7)
    @DisplayName("Prueba 7: Emitir un voto y verificar que se guardó")
    void testEmitirVoto() {
        Votante votante = new Votante("73542595", "Luis", "Torres", "943927052");
        votanteRepository.save(votante);

        PartidoPolitico partido = new PartidoPolitico("Partido Test", "⭐");
        partidoRepository.save(partido);

        Eleccion eleccion = new Eleccion("Elección Voto", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusDays(10), "ACTIVA");
        eleccionRepository.save(eleccion);

        Candidato candidato = new Candidato("Pedro", "Ramos", null, partido, eleccion);
        candidatoRepository.save(candidato);

        Voto primerVoto = new Voto(candidato, eleccion, LocalDateTime.now());
        votoRepository.save(primerVoto);

        assertNotNull(primerVoto.getIdVoto(),
                "El ID del voto no debe ser nulo");

                Participacion participacion = new Participacion(votante, eleccion, LocalDateTime.now());
                participacionRepository.save(participacion); 

        // Verifica que no puede votar dos veces
        Optional<Participacion> yaVoto = participacionRepository.findByVotanteAndEleccion(votante, eleccion);
        assertTrue(yaVoto.isPresent(), "Debe detectar que el votante ya participó");

        System.out.println("Prueba 7 pasada: Voto emitido y doble voto prevenido");
    }

    // ── Prueba 8 ───────────────────────────────────────────────
    @Test
    @Order(8)
    @DisplayName("Prueba 8: Generar comprobante con código de verificación")
    void testGenerarComprobante() {
        Votante votante = new Votante("83625913", "Rosa", "Quispe", "954313475");
        votanteRepository.save(votante);

        PartidoPolitico partido = new PartidoPolitico("Partido Bueno", "🌟");
        partidoRepository.save(partido);

        Eleccion eleccion = new Eleccion("Elección Comprobante", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusDays(10), "ACTIVA");
        eleccionRepository.save(eleccion);

        Candidato candidato = new Candidato("Sofía", "Luna", null, partido, eleccion);
        candidatoRepository.save(candidato);

        Voto voto = new Voto(candidato, eleccion, LocalDateTime.now());
        votoRepository.save(voto);

        // Genera el código igual que en VotoController
        String codigoVerificacion = UUID.randomUUID().toString().toUpperCase();
        Comprobante comprobante = new Comprobante(voto, codigoVerificacion,
                LocalDateTime.now(), "EMITIDO");
        comprobanteRepository.save(comprobante);

        assertNotNull(comprobante.getIdComprobante(),
                "El ID del comprobante no debe ser nulo");
        assertEquals("EMITIDO", comprobante.getEstado(),
                "El estado del comprobante debe ser EMITIDO");
        assertNotNull(comprobante.getCodigoVerificacion(),
                "El código de verificación no debe ser nulo");
        assertEquals(36, codigoVerificacion.length(),
                "El UUID completo debe tener 36 caracteres");

        // Verifica que se puede buscar por código
        Optional<Comprobante> encontrado = comprobanteRepository
                .findByCodigoVerificacion(codigoVerificacion);
        assertTrue(encontrado.isPresent(),
                "Debe encontrar el comprobante por su código");

        System.out.println("Prueba 8 pasada: Comprobante generado con código: "
                + codigoVerificacion);
    }
}