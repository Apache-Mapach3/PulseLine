/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.application;
import com.pulseline.domain.Llamada;
import com.pulseline.domain.repositories.LlamadaRepository;
/**
 *
 * @author Admin
 */
public class RegistroLlamadaService {
    // El gerente tiene un contacto directo con el "bibliotecario" de llamadas.
    private final LlamadaRepository llamadaRepository;

    // Cuando contratamos al gerente, le asignamos su bibliotecario.
    public RegistroLlamadaService(LlamadaRepository llamadaRepository) {
        this.llamadaRepository = llamadaRepository;
    }

    // MÉTODO 1: Un caso de uso -> "Registrar una nueva llamada".
    public void registrarNuevaLlamada(String idLlamada, String idAgente, String idCliente, int duracion) {
        // Paso 1: El gerente crea un nuevo "capitán de equipo" Llamada.
        Llamada nuevaLlamada = new Llamada(idLlamada, idAgente, idCliente, duracion);
        
        // Paso 2: El gerente le dice al bibliotecario: "Guarda este nuevo equipo".
        llamadaRepository.save(nuevaLlamada);
    }
    
    // MÉTODO 2: Otro caso de uso -> "Escalar una llamada existente".
    public void escalarLlamada(String idLlamada, String motivo) {
        // Paso 1: El gerente le pide al bibliotecario que encuentre una Llamada específica.
        Llamada llamada = llamadaRepository.findById(idLlamada)
                .orElseThrow(() -> new RuntimeException("Llamada no encontrada")); // Si no la encuentra, se queja.
        
        // Paso 2: El gerente le da una orden directa al capitán: "¡Escálate con este motivo!".
        llamada.escalar(motivo, java.time.LocalDate.now().plusDays(2));
        
        // Paso 3: El gerente le avisa al bibliotecario: "Guarda los cambios de este equipo".
        llamadaRepository.save(llamada);
    }
}
