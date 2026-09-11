package com.votacion.sistema_votacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "partido_politico")
public class PartidoPolitico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partido")
    private Long idPartido;

    @Column(nullable = false)
    private String nombre;

    private String simbolo;

    // Constructores
    public PartidoPolitico() {
    }

    public PartidoPolitico(String nombre, String simbolo) {
        this.nombre = nombre;
        this.simbolo = simbolo;
    }

    // Getters y Setters
    public Long getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(Long idPartido) {
        this.idPartido = idPartido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
}