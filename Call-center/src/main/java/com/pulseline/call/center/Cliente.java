/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.call.center;

/**
 *
 * @author Admin
 */
public class Cliente {
    
private String id;
    private String nombre;
    private String telefono;
    private String email;

    /**
     * Constructor por defecto.
     */
    public Cliente() {
    }

    /**
     * Constructor para crear un cliente con todos sus datos.
     * @param id El identificador único del cliente.
     * @param nombre El nombre completo del cliente.
     * @param telefono El número de teléfono de contacto.
     * @param email El correo electrónico del cliente.
     */
    public Cliente(String id, String nombre, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
