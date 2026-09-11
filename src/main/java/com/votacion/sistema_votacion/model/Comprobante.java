package com.votacion.sistema_votacion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobante")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long idComprobante;

    @OneToOne
    @JoinColumn(name = "id_voto", nullable = false)
    private Voto voto;

    @Column(name = "codigo_verificacion", nullable = false, unique = true)
    private String codigoVerificacion;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private String estado;

    // Constructores
    public Comprobante() {
    }

    public Comprobante(Voto voto, String codigoVerificacion,
            LocalDateTime fechaEmision, String estado) {
        this.voto = voto;
        this.codigoVerificacion = codigoVerificacion;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Long idComprobante) {
        this.idComprobante = idComprobante;
    }

    public Voto getVoto() {
        return voto;
    }

    public void setVoto(Voto voto) {
        this.voto = voto;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}