package com.votacion.sistema_votacion.controller;

import com.votacion.sistema_votacion.model.Otp;
import com.votacion.sistema_votacion.model.Participacion;
import com.votacion.sistema_votacion.model.Votante;
import com.votacion.sistema_votacion.repository.OtpRepository;
import com.votacion.sistema_votacion.repository.VotanteRepository;
import com.votacion.sistema_votacion.repository.ParticipacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Controller
@RequestMapping("/votantes")
public class VotanteAdminController {

    private static final Logger log = LoggerFactory.getLogger(VotanteAdminController.class);

    @Autowired
    private VotanteRepository votanteRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private ParticipacionRepository participacionRepository;

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        model.addAttribute("votantes", votanteRepository.findAll());
        return "admin/votantes";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String dni,
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String celular,
            HttpSession session,
            Model model) {

        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        // Validar campos obligatorios
        if (dni.trim().isEmpty() ||
                nombres.trim().isEmpty() ||
                apellidos.trim().isEmpty() ||
                celular.trim().isEmpty()) {

            model.addAttribute("votantes", votanteRepository.findAll());
            model.addAttribute("error", "Todos los campos son obligatorios.");
            return "admin/votantes";
        }

        // Verificar si el DNI ya existe
        if (votanteRepository.findByDni(dni).isPresent()) {
            model.addAttribute("votantes", votanteRepository.findAll());
            model.addAttribute("error", "Ya existe un votante con ese DNI");
            log.warn("Intento de registrar DNI duplicado: {}", dni);
            return "admin/votantes";
        }

        String nombresFormateados = capitalizarPalabras(nombres);
        String apellidosFormateados = capitalizarPalabras(apellidos);

        votanteRepository.save(
                new Votante(dni, nombresFormateados, apellidosFormateados, celular));

        log.info("Votante registrado - DNI: {}", dni);

        return "redirect:/votantes";
    }

    private String capitalizarPalabras(String texto) {
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(StringUtils.capitalize(palabra)).append(" ");
            }
        }

        return resultado.toString().trim();
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        Votante votante = votanteRepository.findById(id).orElse(null);
        if (votante == null)
            return "redirect:/votantes";

        // Elimina OTPs del votante
        List<Otp> otps = otpRepository.findAll().stream()
                .filter(o -> o.getVotante().getIdVotante().equals(id))
                .collect(java.util.stream.Collectors.toList());
        otpRepository.deleteAll(otps);

        // Elimina participaciones del votante
        List<Participacion> participaciones = participacionRepository.findAll().stream()
                .filter(p -> p.getVotante().getIdVotante().equals(id))
                .collect(java.util.stream.Collectors.toList());
        participacionRepository.deleteAll(participaciones);

        log.info("Votante eliminado - ID: {}", id);
        votanteRepository.deleteById(id);

        return "redirect:/votantes";
    }
}