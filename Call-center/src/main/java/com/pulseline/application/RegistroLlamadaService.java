package com.pulseline.application;

import com.pulseline.application.dto.LlamadaResponseDTO;
import com.pulseline.application.dto.RegistrarLlamadaRequestDTO;
import com.pulseline.domain.Llamada;
import com.pulseline.domain.repositories.LlamadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Aplicación para gestionar los casos de uso de las Llamadas.
 * CORRECCIÓN: Se agregó @Service para que Spring lo registre como bean inyectable.
 */
@Service
public class RegistroLlamadaService {

    private final LlamadaRepository llamadaRepository;

    public RegistroLlamadaService(LlamadaRepository llamadaRepository) {
        this.llamadaRepository = llamadaRepository;
    }

    /**
     * Caso de uso: Registrar una nueva llamada.
     */
    @Transactional
    public LlamadaResponseDTO registrarNuevaLlamada(RegistrarLlamadaRequestDTO dto) {
        // Validación básica
        if (llamadaRepository.existsById(dto.getNumeroLlamada())) {
            throw new IllegalArgumentException("Ya existe una llamada con el número: " + dto.getNumeroLlamada());
        }

        Llamada nuevaLlamada = new Llamada(
            dto.getNumeroLlamada(),
            dto.getAgenteId(),
            dto.getClienteId(),
            dto.getDuracion()
        );

        Llamada guardada = llamadaRepository.save(nuevaLlamada);
        return LlamadaResponseDTO.fromEntity(guardada);
    }

    /**
     * Caso de uso: Escalar una llamada existente.
     */
    @Transactional
    public LlamadaResponseDTO escalarLlamada(String idLlamada, String motivo) {
        Llamada llamada = llamadaRepository.findById(idLlamada)
                .orElseThrow(() -> new RuntimeException("Llamada no encontrada: " + idLlamada));

        llamada.escalar(motivo, LocalDate.now().plusDays(2));
        Llamada actualizada = llamadaRepository.save(llamada);
        return LlamadaResponseDTO.fromEntity(actualizada);
    }

    /**
     * Caso de uso: Marcar llamada como resuelta.
     */
    @Transactional
    public LlamadaResponseDTO resolverLlamada(String idLlamada) {
        Llamada llamada = llamadaRepository.findById(idLlamada)
                .orElseThrow(() -> new RuntimeException("Llamada no encontrada: " + idLlamada));

        llamada.marcarComoResuelta();
        Llamada actualizada = llamadaRepository.save(llamada);
        return LlamadaResponseDTO.fromEntity(actualizada);
    }

    /**
     * Caso de uso: Consultar todas las llamadas.
     */
    @Transactional(readOnly = true)
    public List<LlamadaResponseDTO> consultarTodasLasLlamadas() {
        return llamadaRepository.findAll()
                .stream()
                .map(LlamadaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Caso de uso: Consultar una llamada por ID.
     */
    @Transactional(readOnly = true)
    public LlamadaResponseDTO consultarLlamadaPorId(String idLlamada) {
        Llamada llamada = llamadaRepository.findById(idLlamada)
                .orElseThrow(() -> new RuntimeException("Llamada no encontrada: " + idLlamada));
        return LlamadaResponseDTO.fromEntity(llamada);
    }
}
