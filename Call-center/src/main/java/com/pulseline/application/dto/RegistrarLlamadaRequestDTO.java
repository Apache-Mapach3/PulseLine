package com.pulseline.application.dto;

public class RegistrarLlamadaRequestDTO {

    private String numeroLlamada;
    private String agenteId;
    private String clienteId;
    private int duracion;

    public String getNumeroLlamada() { return numeroLlamada; }
    public void setNumeroLlamada(String numeroLlamada) { this.numeroLlamada = numeroLlamada; }
    public String getAgenteId() { return agenteId; }
    public void setAgenteId(String agenteId) { this.agenteId = agenteId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }
}
