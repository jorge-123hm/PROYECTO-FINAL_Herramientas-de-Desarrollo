package com.votacion.sistema_votacion.controller;

import com.votacion.sistema_votacion.model.PartidoPolitico;
import com.votacion.sistema_votacion.repository.CandidatoRepository;
import com.votacion.sistema_votacion.repository.PartidoPoliticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/partidos")
public class PartidoPoliticoController {
    private static final Logger log = LoggerFactory.getLogger(PartidoPoliticoController.class);

    @Autowired
    private PartidoPoliticoRepository partidoRepository;

    @Autowired
    private CandidatoRepository candidatoRepository;

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        model.addAttribute("partidos", partidoRepository.findAll());
        return "admin/partidos";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre,
            @RequestParam String simbolo,
            HttpSession session) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        partidoRepository.save(new PartidoPolitico(nombre, simbolo));
        log.info("Partido político registrado: {}", nombre);
        return "redirect:/partidos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        PartidoPolitico partido = partidoRepository.findById(id).orElse(null);
        if (partido == null)
            return "redirect:/partidos";

        // Verificar si tiene candidatos asociados
        long totalCandidatos = candidatoRepository.findAll().stream()
                .filter(c -> c.getPartido().getIdPartido().equals(id))
                .count();

        if (totalCandidatos > 0) {
            redirectAttributes.addFlashAttribute("errorEliminar",
                    "No se puede eliminar el partido '" + partido.getNombre()
                            + "' porque tiene " + totalCandidatos
                            + " candidato(s) asociado(s).");
            return "redirect:/partidos";
        }

        partidoRepository.deleteById(id);
        log.info("Partido político eliminado - ID: {}", id);
        return "redirect:/partidos";
    }
}