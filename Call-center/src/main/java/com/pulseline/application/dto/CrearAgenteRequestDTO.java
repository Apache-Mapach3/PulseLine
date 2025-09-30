/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.application.dto;
import com.pulseline.domain.enums.NivelExperiencia;

public class CrearAgenteRequestDTO {
    private String idAgente;
    private String nombreCompleto;
    private String numeroEmpleado;
    private String email;
    private NivelExperiencia nivelExperiencia;

    // Getters y Setters
    public String getIdAgente() { return idAgente; }
    public void setIdAgente(String idAgente) { this.idAgente = idAgente; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getNumeroEmpleado() { return numeroEmpleado; }
    public void setNumeroEmpleado(String numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) { this.nivelExperiencia = nivelExperiencia; }
}
