package com.votacion.sistema_votacion.controller;

import com.votacion.sistema_votacion.model.*;
import com.votacion.sistema_votacion.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/resultados")
public class ResultadosController {
    private static final Logger log = LoggerFactory.getLogger(ResultadosController.class);

    @Autowired
    private EleccionRepository eleccionRepository;
    @Autowired
    private CandidatoRepository candidatoRepository;
    @Autowired
    private VotoRepository votoRepository;

    // Panel admin — para ver todas las elecciones con sus votos
    @GetMapping("/admin")
    public String resultadosAdmin(HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        model.addAttribute("elecciones", eleccionRepository.findAll());
        log.info("Admin consultó resultados");
        return "admin/resultados";
    }

    // Resultados de una elección específica (admin)
    @GetMapping("/admin/{idEleccion}")
    public String detalleAdmin(@PathVariable Long idEleccion, HttpSession session, Model model) {
        if (session.getAttribute("adminLogueado") == null)
            return "redirect:/admin/login";

        log.info("Admin consultó resultados de elección ID: {}", idEleccion);
        return cargarResultados(idEleccion, model, "admin/resultados-detalle");
    }

    // Resultados publicos - solo para elecciones publicadas
    @GetMapping
    public String resultadosPublicos(Model model) {
        List<Eleccion> publicadas = eleccionRepository.findByPublicadaTrue();
        model.addAttribute("elecciones", publicadas);
        return "resultados";
    }

    // Detalle publico de una elección
    @GetMapping("/{idEleccion}")
    public String detallePublico(@PathVariable Long idEleccion, Model model) {
        Eleccion eleccion = eleccionRepository.findById(idEleccion).orElse(null);
        if (eleccion == null || !eleccion.isPublicada())
            return "redirect:/resultados";

        log.info("Resultados públicos consultados - Elección ID: {}", idEleccion);
        return cargarResultados(idEleccion, model, "resultados-detalle");
    }

    // Exportar resultados a Excel (admin)
    @GetMapping("/admin/{idEleccion}/excel")
    public void exportarExcel(@PathVariable Long idEleccion,
            HttpSession session,
            HttpServletResponse response) throws IOException {

        if (session.getAttribute("adminLogueado") == null) {
            response.sendRedirect("/admin/login");
            return;
        }

        Eleccion eleccion = eleccionRepository.findById(idEleccion)
                .orElseThrow(() -> new IllegalArgumentException("Elección no encontrada"));

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=resultados-eleccion-" + idEleccion + ".xlsx");

        List<Candidato> candidatos = candidatoRepository.findAll().stream()
                .filter(c -> c.getEleccion().getIdEleccion().equals(idEleccion))
                .toList();

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = libro.createSheet("Resultados");

            // Anchos de columnas
            hoja.setColumnWidth(0, 9000);
            hoja.setColumnWidth(1, 6000);
            hoja.setColumnWidth(2, 3000);

            // Estilo con bordes
            CellStyle estiloBorde = libro.createCellStyle();
            estiloBorde.setBorderTop(BorderStyle.THIN);
            estiloBorde.setBorderBottom(BorderStyle.THIN);
            estiloBorde.setBorderLeft(BorderStyle.THIN);
            estiloBorde.setBorderRight(BorderStyle.THIN);

            // Estilo de encabezado
            Font fuenteBlanca = libro.createFont();
            fuenteBlanca.setBold(true);
            fuenteBlanca.setColor(IndexedColors.WHITE.getIndex());

            CellStyle estiloEncabezado = libro.createCellStyle();
            estiloEncabezado.cloneStyleFrom(estiloBorde);
            estiloEncabezado.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloEncabezado.setFont(fuenteBlanca);

            // Título
            Row titulo = hoja.createRow(0);
            titulo.createCell(0).setCellValue("Resultados: " + eleccion.getNombre());

            // Encabezados
            Row encabezado = hoja.createRow(2);
            encabezado.createCell(0).setCellValue("Candidato");
            encabezado.createCell(1).setCellValue("Partido");
            encabezado.createCell(2).setCellValue("Votos");

            for (int columna = 0; columna < 3; columna++) {
                encabezado.getCell(columna).setCellStyle(estiloEncabezado);
            }

            int fila = 3;
            long totalVotos = 0;

            // Resultados por candidato
            for (Candidato candidato : candidatos) {
                long cantidadVotos = votoRepository
                        .countByCandidatoAndEleccion(candidato, eleccion);

                Row datos = hoja.createRow(fila++);
                datos.createCell(0).setCellValue(
                        candidato.getNombres() + " " + candidato.getApellidos());
                datos.createCell(1).setCellValue(candidato.getPartido().getNombre());
                datos.createCell(2).setCellValue(cantidadVotos);

                for (int columna = 0; columna < 3; columna++) {
                    datos.getCell(columna).setCellStyle(estiloBorde);
                }

                totalVotos += cantidadVotos;
            }

            // Votos en blanco
            long votosBlanco = votoRepository.findAll().stream()
                    .filter(v -> v.getEleccion().getIdEleccion().equals(idEleccion))
                    .filter(v -> v.getCandidato() == null)
                    .count();

            if (votosBlanco > 0) {
                Row blanco = hoja.createRow(fila++);
                blanco.createCell(0).setCellValue("Voto en blanco");
                blanco.createCell(1).setCellValue("-");
                blanco.createCell(2).setCellValue(votosBlanco);

                for (int columna = 0; columna < 3; columna++) {
                    blanco.getCell(columna).setCellStyle(estiloBorde);
                }

                totalVotos += votosBlanco;
            }

            // Total
            Row total = hoja.createRow(fila + 1);
            total.createCell(0).setCellValue("TOTAL DE VOTOS");
            total.createCell(1).setCellValue("");
            total.createCell(2).setCellValue(totalVotos);

            for (int columna = 0; columna < 3; columna++) {
                total.getCell(columna).setCellStyle(estiloBorde);
            }

            libro.write(response.getOutputStream());
        }
    }

    // Metodo para cargar datos de los resultados
    private String cargarResultados(Long idEleccion, Model model, String vista) {
        Eleccion eleccion = eleccionRepository.findById(idEleccion).orElse(null);

        List<Candidato> candidatos = candidatoRepository.findAll()
                .stream()
                .filter(c -> c.getEleccion().getIdEleccion().equals(idEleccion))
                .toList();

        long totalVotos = votoRepository.countByEleccion(eleccion);

        // Construye mapa con votos y porcentaje por candidato
        List<Map<String, Object>> datos = new ArrayList<>();
        for (Candidato c : candidatos) {
            long votos = votoRepository.countByCandidatoAndEleccion(c, eleccion);
            double porcentaje = totalVotos > 0 ? (votos * 100.0 / totalVotos) : 0;

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("candidato", c);
            fila.put("votos", votos);
            fila.put("porcentaje", String.format("%.1f", porcentaje));
            datos.add(fila);
        }

        // Votos en blanco
        long votosBlanco = votoRepository.findAll().stream()
                .filter(v -> v.getEleccion().getIdEleccion().equals(idEleccion)
                        && v.getCandidato() == null)
                .count();
        if (votosBlanco > 0) {
            double porcentaje = totalVotos > 0 ? (votosBlanco * 100.0 / totalVotos) : 0;
            Map<String, Object> blanco = new LinkedHashMap<>();
            blanco.put("candidato", null);
            blanco.put("votos", votosBlanco);
            blanco.put("porcentaje", String.format("%.1f", porcentaje));
            datos.add(blanco);
        }

        // Ordena los votos de mayor a menor
        datos.sort((a, b) -> Long.compare((Long) b.get("votos"), (Long) a.get("votos")));

        model.addAttribute("eleccion", eleccion);
        model.addAttribute("datos", datos);
        model.addAttribute("totalVotos", totalVotos);
        return vista;
    }
}