package com.votacion.sistema_votacion.controller;

import com.votacion.sistema_votacion.model.Otp;
import com.votacion.sistema_votacion.model.Participacion;
import com.votacion.sistema_votacion.model.Votante;
import com.votacion.sistema_votacion.repository.OtpRepository;
import com.votacion.sistema_votacion.repository.VotanteRepository;
import com.votacion.sistema_votacion.repository.ParticipacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

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
            @RequestParam(required = false) MultipartFile foto,
            HttpSession session,
            Model model) throws IOException {

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

        // Guardar foto del votante en uploads/ (mismo patrón que fotos de candidato)
        String nombreFoto = null;
        if (foto != null && !foto.isEmpty()) {
            nombreFoto = "votante_" + System.currentTimeMillis() + "_" + foto.getOriginalFilename();
            Path destino = Paths.get("uploads/" + nombreFoto);
            Files.createDirectories(destino.getParent());
            Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        }

        votanteRepository.save(
                new Votante(dni, nombresFormateados, apellidosFormateados, celular, nombreFoto));

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

    // Servir la foto del votante
    @GetMapping("/fotos/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) throws Exception {
        Path path = Paths.get("uploads/" + filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "image/jpeg";
        if (filename.toLowerCase().endsWith(".png"))
            contentType = "image/png";
        if (filename.toLowerCase().endsWith(".webp"))
            contentType = "image/webp";

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(resource);
    }
}