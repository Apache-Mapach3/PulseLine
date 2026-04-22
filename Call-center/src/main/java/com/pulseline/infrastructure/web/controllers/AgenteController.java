package com.pulseline.infrastructure.web.controllers;

import com.pulseline.application.AgenteService;
import com.pulseline.application.dto.AgenteResponseDTO;
import com.pulseline.application.dto.CrearAgenteRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operaciones sobre Agentes.
 * NUEVO: Este controller no existía en el proyecto original,
 * dejando toda la lógica de AgenteService inaccesible por HTTP.
 */
@RestController
@RequestMapping("/api/agentes")
@CrossOrigin(origins = "*")
public class AgenteController {

    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    /** POST /api/agentes — Registrar nuevo agente */
    @PostMapping
    public ResponseEntity<AgenteResponseDTO> registrarAgente(@RequestBody CrearAgenteRequestDTO dto) {
        AgenteResponseDTO respuesta = agenteService.registrarNuevoAgente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /** GET /api/agentes — Listar todos los agentes */
    @GetMapping
    public ResponseEntity<List<AgenteResponseDTO>> listarAgentes() {
        return ResponseEntity.ok(agenteService.consultarTodosLosAgentes());
    }

    /** GET /api/agentes/{id} — Consultar agente por ID */
    @GetMapping("/{id}")
    public ResponseEntity<AgenteResponseDTO> consultarAgente(@PathVariable String id) {
        return ResponseEntity.ok(agenteService.consultarAgentePorId(id));
    }

    /** DELETE /api/agentes/{id} — Eliminar agente */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAgente(@PathVariable String id) {
        agenteService.eliminarAgente(id);
        return ResponseEntity.noContent().build();
    }
}
