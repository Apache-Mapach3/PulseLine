package com.pulseline;

import com.pulseline.ui.PulseLineUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Punto de entrada principal de PulseLine.
 *
 * Secuencia de arranque:
 *   1. Spring Boot inicia el servidor embebido en localhost:8080
 *   2. Una vez listo el contexto, se lanza la UI de escritorio Swing
 *
 * La UI consume su propia API REST — arquitectura limpia y desacoplada.
 */
@SpringBootApplication
public class PulseLineApplication {

    public static void main(String[] args) {
        // 1. Arrancar Spring Boot (API REST + H2 en background)
        ConfigurableApplicationContext context =
            SpringApplication.run(PulseLineApplication.class, args);

        // 2. Lanzar la interfaz gráfica de escritorio
        PulseLineUI.launch();
    }
}
