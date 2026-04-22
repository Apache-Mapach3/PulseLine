package com.pulseline.application;

import com.pulseline.application.dto.AgenteResponseDTO; 
import com.pulseline.application.dto.CampaniaResponseDTO;
import com.pulseline.application.dto.CrearCampaniaRequestDTO;
import com.pulseline.domain.Agente;
import com.pulseline.domain.Campania;
import com.pulseline.domain.repositories.AgenteRepository;
import com.pulseline.domain.repositories.CampaniaRepository;
import com.pulseline.domain.services.AsignacionCampaniaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Aplicación para gestionar los casos de uso de las Campañas.
 * Este servicio estaba completamente ausente en el proyecto original.
 */
@Service
public class CampaniaService {

    private final CampaniaRepository campaniaRepository;
    private final AgenteRepository agenteRepository;
    private final AsignacionCampaniaService asignacionCampaniaService;

    public CampaniaService(CampaniaRepository campaniaRepository,
                           AgenteRepository agenteRepository,
                           AsignacionCampaniaService asignacionCampaniaService) {
        this.campaniaRepository = campaniaRepository;
        this.agenteRepository = agenteRepository;
        this.asignacionCampaniaService = asignacionCampaniaService;
    }

    @Transactional
    public CampaniaResponseDTO crearCampania(CrearCampaniaRequestDTO dto) {
        if (campaniaRepository.existsById(dto.getIdCampania())) {
            throw new IllegalArgumentException("Ya existe una campaña con el ID: " + dto.getIdCampania());
        }
        Campania campania = new Campania(
            dto.getIdCampania(),
            dto.getNombre(),
            dto.getTipo(),
            dto.getFechaInicio(),
            dto.getFechaFin()
        );
        return CampaniaResponseDTO.fromEntity(campaniaRepository.save(campania));
    }

    @Transactional(readOnly = true)
    public List<CampaniaResponseDTO> consultarTodasLasCampanias() {
        return campaniaRepository.findAll()
                .stream()
                .map(CampaniaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CampaniaResponseDTO consultarCampaniaPorId(String idCampania) {
        Campania campania = campaniaRepository.findById(idCampania)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada: " + idCampania));
        return CampaniaResponseDTO.fromEntity(campania);
    }

    /**
     * Caso de uso: Asignar un agente a una campaña.
     * CORRECCIÓN: Ahora persiste la asignación. Antes solo imprimía en consola.
     */
    @Transactional
    public AgenteResponseDTO asignarAgenteACampania(String idAgente, String idCampania) {
        Agente agente = agenteRepository.findById(idAgente)
                .orElseThrow(() -> new RuntimeException("Agente no encontrado: " + idAgente));
        Campania campania = campaniaRepository.findById(idCampania)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada: " + idCampania));

        // El servicio de dominio valida las reglas de negocio
        asignacionCampaniaService.asignarAgenteACampania(agente, campania);

        // Ahora SÍ persiste la asignación en el agente
        agente.asignarACampania(idCampania);
        Agente agenteActualizado = agenteRepository.save(agente);
        return AgenteResponseDTO.fromEntity(agenteActualizado);
    }
}
