package com.pulseline.application.dto;

import com.pulseline.domain.Llamada;
import com.pulseline.domain.enums.ResultadoLlamada;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para exponer datos de una Llamada al cliente.
 * Estaba completamente ausente en el proyecto original.
 */
public class LlamadaResponseDTO {

    private String numeroLlamada;
    private LocalDateTime fechaHora;
    private int duracionSegundos;
    private String idAgente;
    private String idCliente;
    private ResultadoLlamada resultado;
    private String motivoEscalacion;

    private LlamadaResponseDTO() {}

    public static LlamadaResponseDTO fromEntity(Llamada llamada) {
        LlamadaResponseDTO dto = new LlamadaResponseDTO();
        dto.numeroLlamada = llamada.getNumeroLlamada();
        dto.fechaHora = llamada.getFechaHora();
        dto.duracionSegundos = llamada.getDuracionSegundos();
        dto.idAgente = llamada.getIdAgente();
        dto.idCliente = llamada.getIdCliente();
        dto.resultado = llamada.getResultado();
        if (llamada.getEscalacion() != null) {
            dto.motivoEscalacion = llamada.getEscalacion().getMotivo();
        }
        return dto;
    }

    public String getNumeroLlamada() { return numeroLlamada; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public String getIdAgente() { return idAgente; }
    public String getIdCliente() { return idCliente; }
    public ResultadoLlamada getResultado() { return resultado; }
    public String getMotivoEscalacion() { return motivoEscalacion; }
}
