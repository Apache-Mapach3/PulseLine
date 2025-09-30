/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.domain;
import com.pulseline.domain.enums.TipoCampania;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "campanias")
/**
 *
 * @author Admin
 */
public class Campania {
@Id
    private String idCampania;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    protected Campania() {}
    
    public Campania(String idCampania, String nombre, LocalDate inicio, LocalDate fin) {
        this.idCampania = idCampania;
        this.nombre = nombre;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }
    
    // Getters y otros métodos
    public String getIdCampania() { return idCampania; }
    public String getNombre() { return nombre; }

    public TipoCampania getTipo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
