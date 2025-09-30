/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.domain;

import com.pulseline.domain.enums.EstadoEscalacion;
import jakarta.persistence.*;
import java.time.LocalDate;
/**
 *
 * @author Admin
 */

/**
 * Representa una Escalación, una Entidad que es parte del Agregado de Llamada.
 * <p>
 * Esta clase no es un Agregado Raíz, lo que significa que su ciclo de vida está
 * completamente controlado por su entidad padre, {@link Llamada}. No puede existir
 * una escalación sin una llamada asociada.
 * <p>
 * Como entidad JPA, esta clase se mapea a la tabla "escalaciones" en la base de datos.
 *
 * @author Admin
 */
@Entity
@Table(name = "escalaciones")
public class Escalacion {
    
    // --- Atributos de la Entidad ---

    /** Identificador único para la escalación, actúa como Clave Primaria. */
    @Id
    private String numeroEscalacion;
    
    /** Descripción del motivo por el cual la llamada fue escalada. */
    private String motivo;
    
    /** El estado actual de la escalación. Se almacena como texto en la BD. */
    @Enumerated(EnumType.STRING)
    private EstadoEscalacion estado;
    
    /** La fecha límite en la que esta escalación debe ser resuelta. */
    private LocalDate fechaLimite;

    /**
     * Constructor por defecto requerido por el framework JPA.
     * No debe ser usado directamente.
     */
    protected Escalacion() {}

    /**
     * Constructor con visibilidad de paquete (package-private).
     * <p>
     * Esta es una decisión de diseño intencional en DDD. Al no ser público,
     * se prohíbe la creación de instancias de Escalacion desde fuera de su
     * paquete. Se obliga a que la creación sea gestionada únicamente por su
     * Agregado Raíz (la clase {@link Llamada}), protegiendo así las reglas del negocio.
     *
     * @param numeroEscalacion El ID para la nueva escalación.
     * @param motivo La razón de la escalación.
     * @param fechaLimite La fecha máxima para su resolución.
     */
    Escalacion(String numeroEscalacion, String motivo, LocalDate fechaLimite) {
        this.numeroEscalacion = numeroEscalacion;
        this.motivo = motivo;
        this.fechaLimite = fechaLimite;
        this.estado = EstadoEscalacion.PENDIENTE; // Regla de negocio: toda escalación nace como pendiente.
    }

    // --- Comportamientos (Métodos de Negocio) ---

    /**
     * Cambia el estado de la escalación a RESUELTA.
     * Este método encapsula la lógica de negocio para completar una escalación.
     */
    public void marcarComoResuelta() {
        this.estado = EstadoEscalacion.RESUELTA;
    }

    // --- Métodos de Acceso (Getters) ---
    // Permiten la consulta del estado de la entidad desde el exterior.

    public String getNumeroEscalacion() { 
        return numeroEscalacion; 
    }

    public String getMotivo() { 
        return motivo; 
    }

    public EstadoEscalacion getEstado() { 
        return estado; 
    }

    public LocalDate getFechaLimite() { 
        return fechaLimite; 
    }
}