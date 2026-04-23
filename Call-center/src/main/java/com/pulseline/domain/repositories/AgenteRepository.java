
package com.pulseline.domain.repositories;

import com.pulseline.domain.Agente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgenteRepository extends JpaRepository<Agente, String> {
    // ¡No se necesita nada más aquí! Spring hace el resto.
}