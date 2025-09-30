/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.infrastructure.web.controllers;
import com.pulseline.application.RegistroLlamadaService;
import com.pulseline.application.dto.RegistrarLlamadaRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author Admin
 */
/**
 * Controller para gestionar las operaciones web relacionadas con las Llamadas.
 * Esta clase es parte de la Capa de Infraestructura y actúa como el punto de
 * entrada para las solicitudes HTTP. Su única responsabilidad es recibir datos,
 * delegar el trabajo al servicio de aplicación correspondiente y devolver una respuesta.
 */
@RestController // Anotación clave que le dice a Spring que esta clase manejará peticiones web.
@RequestMapping("/api/llamadas") // Define la URL base para todas las operaciones en este controller.
public class LlamadaController {
    // El Controller necesita comunicarse con el "Gerente General" (Servicio de Aplicación).
    private final RegistroLlamadaService registroLlamadaService;

    /**
     * Inyección de dependencias a través del constructor.
     * Spring se encargará automáticamente de "inyectar" o proporcionar una instancia
     * de RegistroLlamadaService cuando se cree el Controller. Es la mejor práctica.
     * @param registroLlamadaService El servicio de aplicación que orquesta el registro de llamadas.
     */
    @Autowired
    public LlamadaController(RegistroLlamadaService registroLlamadaService) {
        this.registroLlamadaService = registroLlamadaService;
    }

    /**
     * Endpoint para registrar una nueva llamada.
     * Escucha las peticiones POST en la URL /api/llamadas/registrar.
     * @param requestDTO La "caja" (DTO) que contiene los datos de la llamada,
     * viene en el cuerpo (body) de la petición HTTP.
     * @return Una respuesta HTTP que indica si la operación fue exitosa.
     */
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarNuevaLlamada(@RequestBody RegistrarLlamadaRequestDTO requestDTO) {
        // 1. El Controller recibe la petición y los datos en el DTO.
        // 2. Llama al servicio de aplicación y le pasa los datos desempaquetados del DTO.
        registroLlamadaService.registrarNuevaLlamada(
            requestDTO.getNumeroLlamada(),
            requestDTO.getAgenteId(),
            requestDTO.getClienteId(),
            requestDTO.getDuracion()
        );
        
        // 3. Devuelve una respuesta exitosa (HTTP 200 OK) con un mensaje.
        return ResponseEntity.ok("Llamada registrada exitosamente.");
    }
}