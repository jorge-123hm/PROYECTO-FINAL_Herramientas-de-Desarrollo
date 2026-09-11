package com.votacion.sistema_votacion.controller;

import com.votacion.sistema_votacion.model.Votante;
import com.votacion.sistema_votacion.model.Otp;
import com.votacion.sistema_votacion.repository.VotanteRepository;
import com.votacion.sistema_votacion.service.RecaptchaService;
import com.votacion.sistema_votacion.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;



@Controller
@RequestMapping("/votante")
public class VotanteController {
    private static final Logger log = LoggerFactory.getLogger(VotanteController.class);

    @Autowired
    private VotanteRepository votanteRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private RecaptchaService recaptchaService;

    // Mostrar login votante (ingresa DNI)
    @GetMapping("/login")
    public String mostrarLogin() {
        return "votante/login";
    }

    // Procesar DNI y generar OTP
    @PostMapping("/login")
    public String procesarDni(@RequestParam String dni, @RequestParam(name = "g-recaptcha-response", required = false) String recaptchaToken,
            HttpSession session, Model model) {

        // Verificar reCAPTCHA
        if (recaptchaToken == null || !recaptchaService.verificar(recaptchaToken)) {
            model.addAttribute("error", "Por favor completa el captcha");
            return "votante/login";
        }

        Votante votante = votanteRepository.findByDni(dni).orElse(null);

        if (votante == null) {
            model.addAttribute("error", "DNI no encontrado");
            log.warn("Intento de login con DNI no encontrado: {}", dni);
            return "votante/login";
        }

        // Invalida todos los OTPs anteriores sin usar
        List<Otp> otpsAnteriores = otpRepository.findAllByVotanteAndUsadoFalse(votante);
        for (Otp otpAnterior : otpsAnteriores) {
            otpAnterior.setUsado(true);
            otpRepository.save(otpAnterior);
        }

        // Generar OTP de 6 dígitos
        String codigo = String.valueOf((int) (Math.random() * 900000) + 100000);
        Otp otp = new Otp(votante, codigo,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        // En producción aquí se enviaría el OTP por SMS
        // Por ahora lo mostramos en consola
        log.info("OTP generado para DNI {}: {}", dni, codigo);


        session.setAttribute("dniVotante", dni);
        session.setAttribute("celularVotante", votante.getCelular());

        model.addAttribute("celular", votante.getCelular());
        model.addAttribute("mensaje", "Se envió el código OTP a tu celular");
        return "votante/verificar-otp";
    }

    // Mostrar página verificar OTP
    @GetMapping("/verificar-otp")
    public String mostrarVerificarOtp(HttpSession session, Model model) {
        String celular = (String) session.getAttribute("celularVotante");
        model.addAttribute("celular", celular);
        return "votante/verificar-otp";
    }

    // Procesar OTP ingresado
    @PostMapping("/verificar-otp")
    public String procesarOtp(@RequestParam String codigo,
            HttpSession session, Model model) {
        String dni = (String) session.getAttribute("dniVotante");

        if (dni == null)
            return "redirect:/votante/login";

        Votante votante = votanteRepository.findByDni(dni).orElse(null);
        if (votante == null)
            return "redirect:/votante/login";

        Otp otp = otpRepository.findByVotanteAndUsadoFalse(votante).orElse(null);

        if (!StringUtils.isNumeric(codigo)) {
            model.addAttribute("error", "El código OTP solo debe contener números");
            return "votante/verificar-otp";
        }

        if (otp == null || !otp.getCodigo().equals(codigo)) {
            model.addAttribute("error", "Código OTP incorrecto");
            log.warn("OTP incorrecto para votante: {}", dni);
            return "votante/verificar-otp";
        }

        if (otp.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "El código OTP ha expirado");
            log.warn("OTP expirado para votante: {}", dni);
            return "votante/verificar-otp";
        }

        // Marcar OTP como usado
        otp.setUsado(true);
        otpRepository.save(otp);

        session.setAttribute("votanteLogueado", votante);
        log.info("Votante autenticado correctamente: {}", dni);
        return "redirect:/voto/elecciones";
    }

    // Cerrar sesión votante
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/votante/login";
    }
}