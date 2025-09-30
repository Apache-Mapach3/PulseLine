/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pulseline.application;
import com.pulseline.application.dto.AgenteResponseDTO;
import com.pulseline.application.dto.CrearAgenteRequestDTO;
import com.pulseline.domain.Agente;
import com.pulseline.domain.repositories.AgenteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Aplicación para gestionar los casos de uso de los Agentes.
 * Este "Gerente" coordina las acciones: recibe datos simples (DTOs),
 * usa el Repositorio para obtener las entidades de dominio y les da órdenes.
 */
@Service
public class AgenteService {

    private final AgenteRepository agenteRepository;

    public AgenteService(AgenteRepository agenteRepository) {
        this.agenteRepository = agenteRepository;
    }

    public AgenteResponseDTO registrarNuevoAgente(CrearAgenteRequestDTO dto) {
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

    public List<AgenteResponseDTO> consultarTodosLosAgentes() {
        return agenteRepository.findAll()
                .stream()
                .map(AgenteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}