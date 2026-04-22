package com.pulseline.infrastructure.web.controllers;

import com.pulseline.application.RegistroLlamadaService;
import com.pulseline.application.dto.LlamadaResponseDTO;
import com.pulseline.application.dto.RegistrarLlamadaRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para operaciones sobre Llamadas.
 * CORRECCIÓN: Ahora retorna DTOs en lugar de Strings planos,
 * y el servicio inyectado tiene @Service correctamente.
 */
@RestController
@RequestMapping("/api/llamadas")
@CrossOrigin(origins = "*")
public class LlamadaController {

    private final RegistroLlamadaService registroLlamadaService;

    public LlamadaController(RegistroLlamadaService registroLlamadaService) {
        this.registroLlamadaService = registroLlamadaService;
    }

    /** POST /api/llamadas — Registrar nueva llamada */
    @PostMapping
    public ResponseEntity<LlamadaResponseDTO> registrarLlamada(@RequestBody RegistrarLlamadaRequestDTO dto) {
        LlamadaResponseDTO respuesta = registroLlamadaService.registrarNuevaLlamada(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /** GET /api/llamadas — Listar todas las llamadas */
    @GetMapping
    public ResponseEntity<List<LlamadaResponseDTO>> listarLlamadas() {
        return ResponseEntity.ok(registroLlamadaService.consultarTodasLasLlamadas());
    }

    /** GET /api/llamadas/{id} — Consultar llamada por ID */
    @GetMapping("/{id}")
    public ResponseEntity<LlamadaResponseDTO> consultarLlamada(@PathVariable String id) {
        return ResponseEntity.ok(registroLlamadaService.consultarLlamadaPorId(id));
    }

    /** PATCH /api/llamadas/{id}/escalar — Escalar una llamada */
    @PatchMapping("/{id}/escalar")
    public ResponseEntity<LlamadaResponseDTO> escalarLlamada(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String motivo = body.get("motivo");
        return ResponseEntity.ok(registroLlamadaService.escalarLlamada(id, motivo));
    }

    /** PATCH /api/llamadas/{id}/resolver — Resolver una llamada */
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<LlamadaResponseDTO> resolverLlamada(@PathVariable String id) {
        return ResponseEntity.ok(registroLlamadaService.resolverLlamada(id));
    }
}
