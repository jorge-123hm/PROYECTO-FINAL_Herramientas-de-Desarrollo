package com.votacion.sistema_votacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidato")
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_candidato")
    private Long idCandidato;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(name = "propuestas_pdf")
    private String propuestasPdf;

    @Column(name = "foto")
    private String foto;

    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false)
    private PartidoPolitico partido;

    @ManyToOne
    @JoinColumn(name = "id_eleccion", nullable = false)
    private Eleccion eleccion;

    // Constructores
    public Candidato() {
    }

    public Candidato(String nombres, String apellidos, String propuestasPdf,
            PartidoPolitico partido, Eleccion eleccion) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.propuestasPdf = propuestasPdf;
        this.partido = partido;
        this.eleccion = eleccion;
    }

    // Getters y Setters
    public Long getIdCandidato() {
        return idCandidato;
    }

    public void setIdCandidato(Long idCandidato) {
        this.idCandidato = idCandidato;
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

    public String getPropuestasPdf() {
        return propuestasPdf;
    }

    public void setPropuestasPdf(String propuestasPdf) {
        this.propuestasPdf = propuestasPdf;
    }

    public String getFoto() { 
        return foto;
    }

    public void setFoto(String foto) { 
        this.foto = foto; 
    }

    public PartidoPolitico getPartido() {
        return partido;
    }

    public void setPartido(PartidoPolitico partido) {
        this.partido = partido;
    }

    public Eleccion getEleccion() {
        return eleccion;
    }

    public void setEleccion(Eleccion eleccion) {
        this.eleccion = eleccion;
    }
}