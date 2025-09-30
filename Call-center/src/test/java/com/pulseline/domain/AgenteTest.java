/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.pulseline.domain;
import com.pulseline.domain.enums.NivelExperiencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del Agregado Raíz Agente")
class AgenteTest {

    @Test
    @DisplayName("Debe crear un agente con datos válidos correctamente")
    void crearAgenteExitosamente() {
        Agente agente = new Agente("A-001", "Juan Perez", "E-123", "juan.perez@email.com", NivelExperiencia.JUNIOR);
        assertNotNull(agente);
        assertEquals("A-001", agente.getIdAgente());
    }

    @Test
    @DisplayName("NO debe crear un agente si el nombre es nulo")
    void noDebeCrearAgenteConNombreNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Agente("A-002", null, "E-124", "otro@email.com", NivelExperiencia.SENIOR);
        });
    }

    @Test
    @DisplayName("Debe actualizar la información de contacto")
    void actualizarInformacionContacto() {
        Agente agente = new Agente("A-003", "Maria Lopez", "E-125", "maria@email.com", NivelExperiencia.INTERMEDIO);
        agente.actualizarInformacionContacto("555-1234", "maria.lopez@email.com");
        assertEquals("555-1234", agente.getTelefono());
        assertEquals("maria.lopez@email.com", agente.getEmail());
    }

    @Test
    @DisplayName("Debe asignar una campaña a un agente")
    void asignarACampania() {
        Agente agente = new Agente("A-004", "Carlos Ruiz", "E-126", "carlos@email.com", NivelExperiencia.JUNIOR);
        agente.asignarACampania("CAMP-VENTAS-01");
        assertEquals(1, agente.getCampaniasAsignadas().size());
        assertTrue(agente.getCampaniasAsignadas().contains("CAMP-VENTAS-01"));
    }
}