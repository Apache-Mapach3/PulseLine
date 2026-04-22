package com.pulseline.domain.repositories;

import com.pulseline.domain.Campania;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para el Agregado Campania.
 * CORRECCIÓN: Se agregó la declaración de package que faltaba completamente.
 */
@Repository
public interface CampaniaRepository extends JpaRepository<Campania, String> {
}
