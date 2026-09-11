package com.votacion.sistema_votacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "votante")
public class Votante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_votante")
    private Long idVotante;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    private String celular;

    // Constructores
    public Votante() {
    }

    public Votante(String dni, String nombres, String apellidos, String celular) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
    }

    // Getters y Setters
    public Long getIdVotante() {
        return idVotante;
    }

    public void setIdVotante(Long idVotante) {
        this.idVotante = idVotante;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}