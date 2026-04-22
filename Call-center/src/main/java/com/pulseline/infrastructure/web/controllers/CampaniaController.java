package com.pulseline.infrastructure.web.controllers;

import com.pulseline.application.CampaniaService;
import com.pulseline.application.dto.AgenteResponseDTO;
import com.pulseline.application.dto.CampaniaResponseDTO;
import com.pulseline.application.dto.CrearCampaniaRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para operaciones sobre Campañas.
 * NUEVO: Este controller no existía en el proyecto original.
 */
@RestController
@RequestMapping("/api/campanias")
@CrossOrigin(origins = "*")
public class CampaniaController {

    private final CampaniaService campaniaService;

    public CampaniaController(CampaniaService campaniaService) {
        this.campaniaService = campaniaService;
    }

    /** POST /api/campanias — Crear nueva campaña */
    @PostMapping
    public ResponseEntity<CampaniaResponseDTO> crearCampania(@RequestBody CrearCampaniaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaniaService.crearCampania(dto));
    }

    /** GET /api/campanias — Listar todas las campañas */
    @GetMapping
    public ResponseEntity<List<CampaniaResponseDTO>> listarCampanias() {
        return ResponseEntity.ok(campaniaService.consultarTodasLasCampanias());
    }

    /** GET /api/campanias/{id} — Consultar campaña por ID */
    @GetMapping("/{id}")
    public ResponseEntity<CampaniaResponseDTO> consultarCampania(@PathVariable String id) {
        return ResponseEntity.ok(campaniaService.consultarCampaniaPorId(id));
    }

    /** POST /api/campanias/{id}/asignar — Asignar agente a campaña */
    @PostMapping("/{id}/asignar")
    public ResponseEntity<AgenteResponseDTO> asignarAgente(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String idAgente = body.get("idAgente");
        return ResponseEntity.ok(campaniaService.asignarAgenteACampania(idAgente, id));
    }
}
