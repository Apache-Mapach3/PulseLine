/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.pulseline.domain;

import com.pulseline.domain.enums.ResultadoLlamada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Agregado Raíz Llamada.
 * Estas pruebas verifican que las reglas de negocio (invariantes)
 * de la clase Llamada se cumplan correctamente.
 */
@DisplayName("Pruebas del Agregado Raíz Llamada")
class LlamadaTest {

    private Llamada llamada;

    @BeforeEach
    void setUp() {
        llamada = new Llamada("L-001", "A-007", "C-123", 120);
    }

    @Test
    @DisplayName("Debe crear una nueva llamada con estado PENDIENTE")
    void unaNuevaLlamadaDebeEstarPendiente() {
        assertEquals(ResultadoLlamada.PENDIENTE, llamada.getResultado(), "Una llamada nueva debería estar en estado PENDIENTE.");
    }

    @Test
    @DisplayName("Debe escalar una llamada pendiente correctamente")
    void escalarLlamadaPendiente() {
        llamada.escalar("El cliente está muy molesto", LocalDate.now().plusDays(1));

        assertEquals(ResultadoLlamada.ESCALADA, llamada.getResultado(), "El estado de la llamada debería ser ESCALADA.");
        assertNotNull(llamada.getEscalacion(), "Debería haberse creado un objeto Escalacion.");
        assertEquals("El cliente está muy molesto", llamada.getEscalacion().getMotivo());
    }

    @Test
    @DisplayName("Debe marcar una llamada como RESUELTA")
    void marcarComoResuelta() {
        llamada.marcarComoResuelta();
        assertEquals(ResultadoLlamada.RESUELTA, llamada.getResultado());
    }

    @Test
    @DisplayName("NO debe permitir escalar una llamada que ya fue resuelta")
    void noDebeEscalarLlamadaResuelta() {
        llamada.marcarComoResuelta();

        assertThrows(IllegalStateException.class, () -> {
            llamada.escalar("Intento de escalar tarde", LocalDate.now());
        }, "Debería lanzar una IllegalStateException al intentar escalar una llamada resuelta.");
    }
}