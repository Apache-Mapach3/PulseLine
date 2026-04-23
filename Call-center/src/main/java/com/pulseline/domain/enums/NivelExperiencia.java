
package com.pulseline.domain.enums;

/**
 * Define los niveles de experiencia que puede tener un agente en el call center.
 * <p>
 * Utilizar un enum en lugar de un String simple previene errores de tipeo
 * y asegura que solo se puedan usar valores predefinidos y válidos.
 *
 * @author Admin
 */
public enum NivelExperiencia {
    /**
     * Nivel de entrada, generalmente para nuevos agentes.
     */
    JUNIOR,

    /**
     * Nivel intermedio, para agentes con cierta experiencia.
     */
    INTERMEDIO,

    /**
     * Nivel más alto, para agentes expertos o supervisores.
     */
    SENIOR
}