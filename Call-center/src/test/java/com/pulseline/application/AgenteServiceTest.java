/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.pulseline.application;
import com.pulseline.application.dto.AgenteResponseDTO;
import com.pulseline.application.dto.CrearAgenteRequestDTO;
import com.pulseline.domain.Agente;
import com.pulseline.domain.enums.NivelExperiencia;
import com.pulseline.domain.repositories.AgenteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Aplicación AgenteService")
class AgenteServiceTest { // <-- ÚNICO DUEÑO DE ESTA CASA

    @Mock
    private AgenteRepository agenteRepository;

    @InjectMocks
    private AgenteService agenteService;

    @Test
    @DisplayName("Debe registrar un nuevo agente exitosamente")
    void registrarNuevoAgente() {
        // Arrange
        CrearAgenteRequestDTO dto = new CrearAgenteRequestDTO();
        dto.setIdAgente("A-TEST-01");
        dto.setNombreCompleto("Agente de Prueba");
        dto.setNumeroEmpleado("E-TEST-01");
        dto.setEmail("test@email.com");
        dto.setNivelExperiencia(NivelExperiencia.JUNIOR);

        Agente agenteGuardado = new Agente("A-TEST-01", "Agente de Prueba", "E-TEST-01", "test@email.com", NivelExperiencia.JUNIOR);

        when(agenteRepository.save(any(Agente.class))).thenReturn(agenteGuardado);

        // Act
        AgenteResponseDTO resultado = agenteService.registrarNuevoAgente(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("A-TEST-01", resultado.getIdAgente());
        verify(agenteRepository, times(1)).save(any(Agente.class));
    }
}