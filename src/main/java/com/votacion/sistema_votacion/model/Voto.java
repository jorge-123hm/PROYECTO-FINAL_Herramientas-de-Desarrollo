package com.votacion.sistema_votacion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voto")
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voto")
    private Long idVoto;

    @ManyToOne
    @JoinColumn(name = "id_candidato", nullable = true)
    private Candidato candidato;

    @ManyToOne
    @JoinColumn(name = "id_eleccion", nullable = false)
    private Eleccion eleccion;

    @Column(name = "fecha_voto")
    private LocalDateTime fechaVoto;

    // Constructores
    public Voto() {
    }

    public Voto(Candidato candidato, Eleccion eleccion, LocalDateTime fechaVoto) {
        this.candidato = candidato;
        this.eleccion = eleccion;
        this.fechaVoto = fechaVoto;
    }

    // Getters y Setters
    public Long getIdVoto() {
        return idVoto;
    }

    public void setIdVoto(Long idVoto) {
        this.idVoto = idVoto;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public Eleccion getEleccion() {
        return eleccion;
    }

    public void setEleccion(Eleccion eleccion) {
        this.eleccion = eleccion;
    }

    public LocalDateTime getFechaVoto() {
        return fechaVoto;
    }

    public void setFechaVoto(LocalDateTime fechaVoto) {
        this.fechaVoto = fechaVoto;
    }
}