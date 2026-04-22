package com.pulseline.application.dto;

import com.pulseline.domain.enums.TipoCampania;
import java.time.LocalDate;

public class CrearCampaniaRequestDTO {

    private String idCampania;
    private String nombre;
    private TipoCampania tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public String getIdCampania() { return idCampania; }
    public void setIdCampania(String idCampania) { this.idCampania = idCampania; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoCampania getTipo() { return tipo; }
    public void setTipo(TipoCampania tipo) { this.tipo = tipo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
