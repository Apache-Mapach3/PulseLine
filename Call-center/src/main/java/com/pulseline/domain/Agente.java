/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.domain;
import com.pulseline.domain.enums.NivelExperiencia;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
/**
 *
 * @author Admin
 */
@Entity
@Table(name = "agentes")
public class Agente {
@Id
    private String idAgente;
    private String nombreCompleto;
    
    @Enumerated(EnumType.STRING) // Guarda el enum como texto en la BD
    private NivelExperiencia nivelExperiencia;
    
    protected Agente() {}
    
    public Agente(String idAgente, String nombreCompleto, NivelExperiencia nivel) {
        this.idAgente = idAgente;
        this.nombreCompleto = nombreCompleto;
        this.nivelExperiencia = nivel;
    }
    
    // Getters
    public String getIdAgente() { return idAgente; }
    public String getNombreCompleto() { return nombreCompleto; }
    
    // ¡GETTER AÑADIDO!
    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
}