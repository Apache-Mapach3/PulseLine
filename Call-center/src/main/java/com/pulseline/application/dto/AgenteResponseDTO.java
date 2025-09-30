/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.application.dto;

/**
 *
 * @author Admin
 */
import com.pulseline.domain.Agente;
import com.pulseline.domain.enums.NivelExperiencia;
import java.util.List;

public class AgenteResponseDTO {
    private String idAgente;
    private String nombreCompleto;
    private String email;
    private NivelExperiencia nivelExperiencia;
    private List<String> campaniasAsignadas;

    private AgenteResponseDTO() {}

    public static AgenteResponseDTO fromEntity(Agente agente) {
        AgenteResponseDTO dto = new AgenteResponseDTO();
        dto.idAgente = agente.getIdAgente();
        dto.nombreCompleto = agente.getNombreCompleto();
        dto.email = agente.getEmail();
        dto.nivelExperiencia = agente.getNivelExperiencia();
        dto.campaniasAsignadas = agente.getCampaniasAsignadas();
        return dto;
    }

    // Getters
    public String getIdAgente() { return idAgente; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
    public List<String> getCampaniasAsignadas() { return campaniasAsignadas; }
}