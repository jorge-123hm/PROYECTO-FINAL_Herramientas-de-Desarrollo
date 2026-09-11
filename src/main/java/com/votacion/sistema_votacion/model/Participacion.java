package com.votacion.sistema_votacion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participacion")
public class Participacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participacion")
    private Long idParticipacion;

    @ManyToOne
    @JoinColumn(name = "id_votante", nullable = false)
    private Votante votante;

    @ManyToOne
    @JoinColumn(name = "id_eleccion", nullable = false)
    private Eleccion eleccion;

    @Column(name = "fecha_participacion")
    private LocalDateTime fechaParticipacion;

    // Constructores
    public Participacion() {
    }

    public Participacion(Votante votante, Eleccion eleccion, LocalDateTime fecha) {
        this.votante = votante;
        this.eleccion = eleccion;
        this.fechaParticipacion = fecha;
    }

    // Getters y Setters
    public Long getIdParticipacion() {
        return idParticipacion;
    }

    public void setIdParticipacion(Long idParticipacion) {
        this.idParticipacion = idParticipacion;
    }

    public Votante getVotante() {
        return votante;
    }

    public void setVotante(Votante votante) {
        this.votante = votante;
    }

    public Eleccion getEleccion() {
        return eleccion;
    }

    public void setEleccion(Eleccion eleccion) {
        this.eleccion = eleccion;
    }

    public LocalDateTime getFechaParticipacion() {
        return fechaParticipacion;
    }

    public void setFechaParticipacion(LocalDateTime fechaParticipacion) {
        this.fechaParticipacion = fechaParticipacion;
    }
}