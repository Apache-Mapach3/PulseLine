package com.pulseline.domain;

import com.pulseline.domain.enums.ResultadoLlamada;
import jakarta.persistence.*; // Importante: usa jakarta.persistence
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 *
 * @author Admin
 */


/**
 * Representa el Agregado Raíz (Aggregate Root) de una Llamada.
 * <p>
 * Esta clase es el "capitán" que controla toda la lógica de negocio y el ciclo 
 * de vida de una llamada y su posible escalación. La regla principal es que 
 * cualquier cambio en la llamada o en su escalación debe pasar por un método 
 * de esta clase para garantizar la consistencia de los datos.
 * <p>
 * Como entidad JPA, esta clase se mapea a la tabla "llamadas" en la base de datos.
 *
 * @author Admin
 */
@Entity
@Table(name = "llamadas")
public class Llamada {

    // --- Atributos del Agregado ---

    /** Identificador único y de negocio para la llamada, actúa como Clave Primaria. */
    @Id
    private String numeroLlamada;

    /** Fecha y hora en que se creó la llamada. Se establece automáticamente. */
    private LocalDateTime fechaHora;

    /** Duración de la llamada en segundos. */
    private int duracionSegundos;
    
    /**
     * Referencia al Agente por su ID.
     * En DDD, los agregados no deben tener referencias directas a otros agregados,
     * solo a sus IDs para mantener un bajo acoplamiento.
     */
    private String idAgente;
    
    /** Referencia al Cliente por su ID. */
    private String idCliente;
    
    /** * Estado actual de la llamada. Se almacena como un String en la base de datos
     * (ej. "RESUELTA") para mayor claridad.
     */
    @Enumerated(EnumType.STRING)
    private ResultadoLlamada resultado;
    
    /**
     * La entidad hija Escalacion, parte de este agregado.
     * La relación @OneToOne asegura que una llamada solo puede tener una escalación.
     * CascadeType.ALL significa que cualquier operación (guardar, borrar) sobre la Llamada
     * se aplicará también a su Escalacion asociada.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Escalacion escalacion;

    /**
     * Constructor por defecto requerido por el framework JPA.
     * No debe ser usado directamente, por eso es 'protected'.
     */
    protected Llamada() {}

    /**
     * Constructor para crear una nueva instancia de Llamada.
     * Establece el estado inicial de toda llamada como PENDIENTE.
     *
     * @param numeroLlamada El identificador único para la nueva llamada.
     * @param idAgente El ID del agente que atiende la llamada.
     * @param idCliente El ID del cliente asociado.
     * @param duracionSegundos La duración total de la llamada en segundos.
     */
    public Llamada(String numeroLlamada, String idAgente, String idCliente, int duracionSegundos) {
        this.numeroLlamada = numeroLlamada;
        this.idAgente = idAgente;
        this.idCliente = idCliente;
        this.duracionSegundos = duracionSegundos;
        this.fechaHora = LocalDateTime.now();
        this.resultado = ResultadoLlamada.PENDIENTE; // Regla de negocio: toda llamada inicia en estado pendiente.
    }

    // --- Comportamientos (Métodos de Negocio) ---

    /**
     * Marca la llamada como resuelta.
     * Esta es una operación de negocio que cambia el estado del agregado y asegura
     * que una llamada resuelta no puede tener una escalación activa (invariante).
     */
    public void marcarComoResuelta() {
        this.resultado = ResultadoLlamada.RESUELTA;
        this.escalacion = null; // Invariante: si se resuelve, se elimina la posible escalación.
    }
    
    /**
     * Escala la llamada, creando una entidad Escalacion hija.
     * Este método protege una regla de negocio clave: una llamada resuelta no puede ser escalada.
     *
     * @param motivo La razón por la cual la llamada está siendo escalada.
     * @param fechaLimite La fecha máxima para resolver la escalación.
     * @throws IllegalStateException si se intenta escalar una llamada que ya fue resuelta.
     */
    public void escalar(String motivo, LocalDate fechaLimite) {
        if (this.resultado == ResultadoLlamada.RESUELTA) {
            // Protección de invariante: No permitir acciones inconsistentes.
            throw new IllegalStateException("No se puede escalar una llamada que ya fue resuelta.");
        }
        this.resultado = ResultadoLlamada.ESCALADA;
        
        // El Agregado Raíz es el único responsable de crear sus entidades hijas.
        this.escalacion = new Escalacion("ESC-" + this.numeroLlamada, motivo, fechaLimite);
    }
    
    // --- Métodos de Acceso (Getters) ---
    // Proveen acceso de solo lectura a los datos del agregado.

    public String getNumeroLlamada() { return numeroLlamada; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public String getIdAgente() { return idAgente; }
    public String getIdCliente() { return idCliente; }
    public ResultadoLlamada getResultado() { return resultado; }
    public Escalacion getEscalacion() { return escalacion; }
}