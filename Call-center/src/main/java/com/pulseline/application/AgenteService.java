package com.pulseline.application;

import com.pulseline.application.dto.AgenteResponseDTO;
import com.pulseline.application.dto.CrearAgenteRequestDTO;
import com.pulseline.domain.Agente;
import com.pulseline.domain.repositories.AgenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de Aplicación para gestionar los casos de uso de los Agentes.
 */
@Service
public class AgenteService {

    private final AgenteRepository agenteRepository;

    public AgenteService(AgenteRepository agenteRepository) {
        this.agenteRepository = agenteRepository;
    }

    @Transactional
    public AgenteResponseDTO registrarNuevoAgente(CrearAgenteRequestDTO dto) {
        if (agenteRepository.existsById(dto.getIdAgente())) {
            throw new IllegalArgumentException("Ya existe un agente con el ID: " + dto.getIdAgente());
        }

        Agente nuevoAgente = new Agente(
            dto.getIdAgente(),
            dto.getNombreCompleto(),
            dto.getNumeroEmpleado(),
            dto.getEmail(),
            dto.getNivelExperiencia()
        );

        Agente agenteGuardado = agenteRepository.save(nuevoAgente);
        return AgenteResponseDTO.fromEntity(agenteGuardado);
    }

    @Transactional(readOnly = true)
    public List<AgenteResponseDTO> consultarTodosLosAgentes() {
        return agenteRepository.findAll()
                .stream()
                .map(AgenteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgenteResponseDTO consultarAgentePorId(String idAgente) {
        Agente agente = agenteRepository.findById(idAgente)
                .orElseThrow(() -> new RuntimeException("Agente no encontrado: " + idAgente));
        return AgenteResponseDTO.fromEntity(agente);
    }

    @Transactional
    public AgenteResponseDTO actualizarContacto(String idAgente, String telefono, String email) {
        Agente agente = agenteRepository.findById(idAgente)
                .orElseThrow(() -> new RuntimeException("Agente no encontrado: " + idAgente));
        agente.actualizarInformacionContacto(telefono, email);
        return AgenteResponseDTO.fromEntity(agenteRepository.save(agente));
    }

    @Transactional
    public void eliminarAgente(String idAgente) {
        if (!agenteRepository.existsById(idAgente)) {
            throw new RuntimeException("Agente no encontrado: " + idAgente);
        }
        agenteRepository.deleteById(idAgente);
    }
}
