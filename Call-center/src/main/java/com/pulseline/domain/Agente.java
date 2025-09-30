/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.domain;

import com.pulseline.domain.enums.NivelExperiencia;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agentes")
public class Agente { // <-- Este archivo solo debe tener esta clase

    @Id
    private String idAgente;
    private String nombreCompleto;
    private String numeroEmpleado;
    private String telefono;
    private String email;

    @Enumerated(EnumType.STRING)
    private NivelExperiencia nivelExperiencia;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agente_campanias", joinColumns = @JoinColumn(name = "id_agente"))
    @Column(name = "id_campania")
    private List<String> campaniasAsignadas = new ArrayList<>();

    protected Agente() {}

    public Agente(String idAgente, String nombreCompleto, String numeroEmpleado, String email, NivelExperiencia nivel) {
        if (idAgente == null || nombreCompleto == null || numeroEmpleado == null) {
            throw new IllegalArgumentException("ID, Nombre y Número de Empleado son obligatorios.");
        }
        this.idAgente = idAgente;
        this.nombreCompleto = nombreCompleto;
        this.numeroEmpleado = numeroEmpleado;
        this.email = email;
        this.nivelExperiencia = nivel;
    }

    // --- Métodos de Negocio y Getters ---
    public void actualizarInformacionContacto(String nuevoTelefono, String nuevoEmail) {
        this.telefono = nuevoTelefono;
        this.email = nuevoEmail;
    }

    public void asignarACampania(String idCampania) {
        if (!this.campaniasAsignadas.contains(idCampania)) {
            this.campaniasAsignadas.add(idCampania);
        }
    }
    
    public String getIdAgente() { return idAgente; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getNumeroEmpleado() { return numeroEmpleado; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
    public List<String> getCampaniasAsignadas() { return campaniasAsignadas; }
}