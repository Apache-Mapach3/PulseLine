package com.pulseline.domain.services;

import com.pulseline.domain.Agente;
import com.pulseline.domain.Campania;
import com.pulseline.domain.enums.NivelExperiencia;
import com.pulseline.domain.enums.TipoCampania;
import org.springframework.stereotype.Service;

/**
 * Servicio de Dominio que encapsula reglas de negocio que involucran
 * múltiples agregados (Agente y Campania).
 */
@Service
public class AsignacionCampaniaService {

    /**
     * Valida si un agente puede ser asignado a una campaña según las reglas de negocio.
     * CORRECCIÓN: Ya no imprime en consola. Lanza excepciones descriptivas para
     * cada regla violada, permitiendo que la capa de aplicación maneje la respuesta.
     *
     * @param agente   El Agregado Agente.
     * @param campania El Agregado Campania.
     * @throws IllegalStateException si la asignación viola una regla de negocio.
     */
    public void asignarAgenteACampania(Agente agente, Campania campania) {

        // REGLA 1: Un agente JUNIOR no puede ser asignado a campañas de COBRANZAS.
        if (agente.getNivelExperiencia() == NivelExperiencia.JUNIOR &&
            campania.getTipo() == TipoCampania.COBRANZAS) {
            throw new IllegalStateException(
                "Regla de negocio violada: El agente '" + agente.getNombreCompleto() +
                "' es JUNIOR y no puede ser asignado a la campaña de COBRANZAS '" + campania.getNombre() + "'."
            );
        }

        // REGLA 2: Un agente no puede ser asignado a más de 3 campañas simultáneas.
        if (agente.getCampaniasAsignadas().size() >= 3) {
            throw new IllegalStateException(
                "Regla de negocio violada: El agente '" + agente.getNombreCompleto() +
                "' ya tiene el máximo de 3 campañas asignadas."
            );
        }

        // REGLA 3: No asignar si ya está en esa campaña (evitar duplicados).
        if (agente.getCampaniasAsignadas().contains(campania.getIdCampania())) {
            throw new IllegalStateException(
                "El agente '" + agente.getNombreCompleto() +
                "' ya está asignado a la campaña '" + campania.getNombre() + "'."
            );
        }
    }
}
