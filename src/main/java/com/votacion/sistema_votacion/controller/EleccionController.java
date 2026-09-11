package com.votacion.sistema_votacion.controller;

import com.votacion.sistema_votacion.model.*;
import com.votacion.sistema_votacion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/elecciones")
public class EleccionController {

    private static final Logger log = LoggerFactory.getLogger(EleccionController.class);

    @Autowired
    private EleccionRepository eleccionRepository;
    @Autowired 
    private CandidatoRepository candidatoRepository;
    @Autowired 
    private VotoRepository votoRepository;
    @Autowired 
    private ComprobanteRepository comprobanteRepository;
    @Autowired 
    private ParticipacionRepository participacionRepository;

    // Listar elecciones (admin)
    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        model.addAttribute("elecciones", eleccionRepository.findAll());
        return "admin/elecciones";
    }

    // Guardar nueva elección
    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam String estado,
            HttpSession session) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        Eleccion eleccion = new Eleccion(
                nombre, descripcion,
                LocalDateTime.parse(fechaInicio),
                LocalDateTime.parse(fechaFin),
                estado);
        eleccionRepository.save(eleccion);
        log.info("Elección creada: {}", nombre);
        return "redirect:/elecciones";
    }

    // Cambiar estado de elección
    @GetMapping("/estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable long id,
            @PathVariable String estado,
            HttpSession session) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        eleccionRepository.findById(id).ifPresent(e -> {
            e.setEstado(estado);
            eleccionRepository.save(e);
        });
        log.info("Estado de elección {} cambiado a: {}", id, estado);
        return "redirect:/elecciones";
    }

    // Eliminar elección
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable long id, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        Eleccion eleccion = eleccionRepository.findById(id).orElse(null);
        if (eleccion == null) return "redirect:/elecciones";

    // 1. Eliminar comprobantes de los votos(de la eleccion)
    List<Voto> votos = votoRepository.findAll().stream()
        .filter(v -> v.getEleccion().getIdEleccion().equals(id))
        .collect(java.util.stream.Collectors.toList());

    for (Voto voto : votos) {
        comprobanteRepository.findAll().stream()
            .filter(c -> c.getVoto().getIdVoto().equals(voto.getIdVoto()))
            .forEach(comprobanteRepository::delete);
    }

    // 2. Eliminar votos(de la eleccion)
    votoRepository.deleteAll(votos);

    // 3. Eliminar participaciones
    List<Participacion> participaciones = participacionRepository.findAll().stream()
        .filter(p -> p.getEleccion().getIdEleccion().equals(id))
        .collect(java.util.stream.Collectors.toList());
    participacionRepository.deleteAll(participaciones);

    // 4. Eliminar candidatos(dela eleccion)
    List<Candidato> candidatos = candidatoRepository.findAll().stream()
        .filter(c -> c.getEleccion().getIdEleccion().equals(id))
        .collect(java.util.stream.Collectors.toList());
    candidatoRepository.deleteAll(candidatos);
    //5. Eliminar la eleccion
        eleccionRepository.deleteById(id);
        log.info("Elección eliminada - ID: {}", id);
        return "redirect:/elecciones";
    }

    // Verificar contraseña del admin logueado
    @GetMapping("/verificar-password")
    @ResponseBody
    public Map<String, Boolean> verificarPassword(@RequestParam String password,HttpSession session) {
    Administrador admin = (Administrador) session.getAttribute("adminLogueado");
    boolean valido = admin != null && admin.getPassword().equals(password);
    return Map.of("valido", valido);
}

    // Publicar resultados de una elección
    @GetMapping("/publicar/{id}")
    public String publicar(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        eleccionRepository.findById(id).ifPresent(e -> {
            e.setPublicada(true);
            eleccionRepository.save(e);
        });
        log.info("Resultados publicados para elección ID: {}", id);
        return "redirect:/elecciones";
    }
}