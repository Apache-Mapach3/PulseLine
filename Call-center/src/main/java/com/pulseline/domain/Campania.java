package com.pulseline.domain;
import com.pulseline.domain.enums.TipoCampania;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "campanias")
public class Campania {
    @Id
    private String idCampania;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoCampania tipo;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    protected Campania() {}

    public Campania(String idCampania, String nombre, TipoCampania tipo, LocalDate inicio, LocalDate fin) {
        this.idCampania = idCampania;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    // --- Getters ---
    public String getIdCampania() { return idCampania; }
    public String getNombre() { return nombre; }
    public TipoCampania getTipo() { return tipo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
}