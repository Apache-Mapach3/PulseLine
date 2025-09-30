/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.pulseline.domain;
import com.pulseline.domain.enums.TipoCampania;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Agregado Raíz Campania.
 * Verifica que una Campania se pueda crear con sus datos correctos.
 */
@DisplayName("Pruebas del Agregado Raíz Campania")
class CampaniaTest {

    @Test
    @DisplayName("Debe crear una campaña con todos sus atributos correctamente")
    void crearCampaniaCorrectamente() {
        // Arrange & Act
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().plusDays(30);
        Campania campania = new Campania("CAMP-01", "Ventas de Verano", TipoCampania.VENTAS, inicio, fin);

        // Assert
        assertNotNull(campania);
        assertEquals("CAMP-01", campania.getIdCampania());
        assertEquals("Ventas de Verano", campania.getNombre());
        assertEquals(TipoCampania.VENTAS, campania.getTipo());
        assertEquals(inicio, campania.getFechaInicio());
    }
}