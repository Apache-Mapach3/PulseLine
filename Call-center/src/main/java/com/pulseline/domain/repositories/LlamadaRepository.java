
package com.pulseline.domain.repositories;

/**
 *
 * @author Admin
 */
import com.pulseline.domain.Llamada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LlamadaRepository extends JpaRepository<Llamada, String> {
}