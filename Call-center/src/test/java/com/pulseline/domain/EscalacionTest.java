/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.pulseline.domain;
import com.pulseline.domain.enums.EstadoEscalacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de la Entidad Escalacion")
class EscalacionTest {

    @Test
    @DisplayName("Debe crear una escalación con estado PENDIENTE")
    void crearEscalacionConEstadoInicialCorrecto() {
        // Arrange & Act
        Escalacion escalacion = new Escalacion("ESC-001", "Cliente molesto", LocalDate.now().plusDays(3));

        // Assert
        assertEquals(EstadoEscalacion.PENDIENTE, escalacion.getEstado(), "Una nueva escalación debe estar PENDIENTE.");
        assertEquals("ESC-001", escalacion.getNumeroEscalacion());
    }

    @Test
    @DisplayName("Debe cambiar el estado a RESUELTA al marcarla como resuelta")
    void marcarComoResuelta() {
        // Arrange
        Escalacion escalacion = new Escalacion("ESC-001", "Cliente molesto", LocalDate.now().plusDays(3));

        // Act
        escalacion.marcarComoResuelta();

        // Assert
        assertEquals(EstadoEscalacion.RESUELTA, escalacion.getEstado(), "El estado debería cambiar a RESUELTA.");
    }
}
