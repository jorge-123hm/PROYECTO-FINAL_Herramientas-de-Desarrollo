package com.votacion.sistema_votacion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_otp")
    private Long idOtp;

    @ManyToOne
    @JoinColumn(name = "id_votante", nullable = false)
    private Votante votante;

    @Column(nullable = false)
    private String codigo;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private boolean usado = false;

    // Constructores
    public Otp() {
    }

    public Otp(Votante votante, String codigo,
            LocalDateTime fechaGeneracion, LocalDateTime fechaExpiracion) {
        this.votante = votante;
        this.codigo = codigo;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = false;
    }

    // Getters y Setters
    public Long getIdOtp() {
        return idOtp;
    }

    public void setIdOtp(Long idOtp) {
        this.idOtp = idOtp;
    }

    public Votante getVotante() {
        return votante;
    }

    public void setVotante(Votante votante) {
        this.votante = votante;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}