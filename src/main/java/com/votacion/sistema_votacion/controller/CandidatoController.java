package com.votacion.sistema_votacion.controller;

import com.votacion.sistema_votacion.model.Candidato;
import com.votacion.sistema_votacion.model.Eleccion;
import com.votacion.sistema_votacion.model.PartidoPolitico;
import com.votacion.sistema_votacion.repository.CandidatoRepository;
import com.votacion.sistema_votacion.repository.EleccionRepository;
import com.votacion.sistema_votacion.repository.VotoRepository;
import com.votacion.sistema_votacion.repository.PartidoPoliticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/candidatos")
public class CandidatoController {

    private static final Logger log = LoggerFactory.getLogger(CandidatoController.class);

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Autowired
    private EleccionRepository eleccionRepository;

    @Autowired
    private PartidoPoliticoRepository partidoRepository;

    @Autowired
    private VotoRepository votoRepository;

    // ── Vista pública de candidatos
    @GetMapping
    public String listarCandidatos(Model model) {
        List<Candidato> candidatos = candidatoRepository.findAllByOrderByApellidosAsc();
        model.addAttribute("candidatos", candidatos);
        return "candidatos";
    }

    // ── Vista admin de candidatos
    @GetMapping("/admin")
    public String listarAdmin(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        model.addAttribute("candidatos", candidatoRepository.findAll());
        model.addAttribute("elecciones", eleccionRepository.findAll());
        model.addAttribute("partidos", partidoRepository.findAll());
        return "admin/candidatos";
    }

    // ── Guardar nuevo candidato
    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam long idPartido,
            @RequestParam long idEleccion,
            @RequestParam(required = false) MultipartFile propuestasPdf,
            @RequestParam(required = false) MultipartFile foto,
            HttpSession session) throws IOException {

        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        PartidoPolitico partido = partidoRepository.findById(idPartido).orElse(null);
        Eleccion eleccion = eleccionRepository.findById(idEleccion).orElse(null);

        // Guardar el PDF en la carpeta uploads/
        String nombreArchivo = null;
        if (propuestasPdf != null && !propuestasPdf.isEmpty()) {
            nombreArchivo = System.currentTimeMillis() + "_" + propuestasPdf.getOriginalFilename();
            Path destino = Paths.get("uploads/" + nombreArchivo);
            Files.createDirectories(destino.getParent());
            Files.copy(propuestasPdf.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        }

        //Guardar foto de candidato
        String nombreFoto = null;
        if (foto != null && !foto.isEmpty()) {
            nombreFoto = "foto_" + System.currentTimeMillis() + "_" + foto.getOriginalFilename();
            Path destino = Paths.get("uploads/" + nombreFoto);
            Files.createDirectories(destino.getParent());
            Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        }

        Candidato candidato = new Candidato(nombres, apellidos, nombreArchivo, partido, eleccion);
        log.info("Candidato registrado: {} {}", nombres, apellidos);
        candidato.setFoto(nombreFoto);
        candidatoRepository.save(candidato);
        return "redirect:/candidatos/admin";
    }

    // ── Servir archivos PDF
    @GetMapping("/archivos/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> verArchivo(@PathVariable String filename) throws Exception {
        Path path = Paths.get("uploads/" + filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .body(resource);
    }

    //Servir foto 
    @GetMapping("/fotos/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) throws Exception {
        Path path     = Paths.get("uploads/" + filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Detecta el tipo de imagen automáticamente
        String contentType = "image/jpeg";
        if (filename.toLowerCase().endsWith(".png"))  contentType = "image/png";
        if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";

        return ResponseEntity.ok()
            .header("Content-Type", contentType)
            .body(resource);
    }

    // Eliminar candidato
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        Candidato candidato = candidatoRepository.findById(id).orElse(null);
        if (candidato == null) return "redirect:/candidatos/admin";

    // Verificar si tiene votos asociados
    long totalVotos = votoRepository.countByCandidatoAndEleccion(
        candidato, candidato.getEleccion()
    );

    if (totalVotos > 0) {
        redirectAttributes.addFlashAttribute("errorEliminar",
            "No se puede eliminar a " + candidato.getNombres() + " " 
            + candidato.getApellidos() + " porque tiene " + totalVotos 
            + " voto(s) registrado(s). Primero elimina el proceso electoral: "
            + candidato.getEleccion().getNombre());
            log.warn("Intento de eliminar candidato con votos - ID: {}", id);
        return "redirect:/candidatos/admin";
    }

        candidatoRepository.deleteById(id);
        log.info("Candidato eliminado - ID: {}", id);
        return "redirect:/candidatos/admin";
    }
}