package com.pulseline.application.dto;

import com.pulseline.domain.Campania;
import com.pulseline.domain.enums.TipoCampania;

import java.time.LocalDate;

public class CampaniaResponseDTO {

    private String idCampania;
    private String nombre;
    private TipoCampania tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private CampaniaResponseDTO() {}

    public static CampaniaResponseDTO fromEntity(Campania campania) {
        CampaniaResponseDTO dto = new CampaniaResponseDTO();
        dto.idCampania = campania.getIdCampania();
        dto.nombre = campania.getNombre();
        dto.tipo = campania.getTipo();
        dto.fechaInicio = campania.getFechaInicio();
        dto.fechaFin = campania.getFechaFin();
        return dto;
    }

    public String getIdCampania() { return idCampania; }
    public String getNombre() { return nombre; }
    public TipoCampania getTipo() { return tipo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
}
