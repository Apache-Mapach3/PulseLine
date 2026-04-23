package com.pulseline;

import com.pulseline.ui.PulseLineUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PulseLineApplication {

    public static void main(String[] args) {
        // ← AGREGAR ESTA LÍNEA — debe ser lo PRIMERO antes de todo
        System.setProperty("java.awt.headless", "false");

        // 1. Arrancar Spring Boot
        ConfigurableApplicationContext context =
            SpringApplication.run(PulseLineApplication.class, args);

        // 2. Lanzar la interfaz gráfica
        PulseLineUI.launch();
    }
}