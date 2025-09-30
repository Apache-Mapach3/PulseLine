/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.application.dto;

/**
 *
 * @author Admin
 */
public class RegistrarLlamadaRequestDTO {
       private String numeroLlamada;
    private String agenteId;
    private String clienteId;
    private int duracion;

    // --- Getters (para obtener los datos) ---

    public String getNumeroLlamada() {
        return numeroLlamada;
    }

    public String getAgenteId() {
        return agenteId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public int getDuracion() {
        return duracion;
    }

    // --- Setters (para establecer los datos) ---

    public void setNumeroLlamada(String numeroLlamada) {
        this.numeroLlamada = numeroLlamada;
    }

    public void setAgenteId(String agenteId) {
        this.agenteId = agenteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
}
