/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.domain.services;
import com.pulseline.domain.Agente;
import com.pulseline.domain.Campania;
import com.pulseline.domain.enums.NivelExperiencia;
import com.pulseline.domain.enums.TipoCampania;
import org.springframework.stereotype.Service;
/**
 *
 * @author Admin
 */

@Service
public class AsignacionCampaniaService {
    
    /**
     * Asigna un agente a una campaña, validando las reglas de negocio.
     * @param agente El Agregado Agente.
     * @param campania El Agregado Campania.
     * @throws IllegalStateException si la asignación viola una regla de negocio.
     */
    public void asignarAgenteACampania(Agente agente, Campania campania) {
        
        // REGLA DE NEGOCIO: "Un agente JUNIOR no puede ser asignado a campañas de COBRANZAS".
        if (agente.getNivelExperiencia() == NivelExperiencia.JUNIOR && 
            campania.getTipo() == TipoCampania.COBRANZAS) {
            throw new IllegalStateException("Regla de negocio violada: Agentes Junior no pueden ser asignados a campañas de cobranzas.");
        }
        
        // Si todo está bien, la lógica para registrar la asignación iría aquí.
        System.out.println("Agente " + agente.getNombreCompleto() + " asignado exitosamente a la campaña " + campania.getNombre());
    }
}