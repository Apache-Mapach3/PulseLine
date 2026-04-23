package com.pulseline;

import com.pulseline.ui.LoginFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PulseLineApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        // Arrancar Spring Boot
        ConfigurableApplicationContext context =
            SpringApplication.run(PulseLineApplication.class, args);

        // Lanzar pantalla de LOGIN (no el MainFrame directamente)
        javax.swing.SwingUtilities.invokeLater(() -> {
            com.pulseline.ui.PulseLineUI.setupLookAndFeelStatic();
            new LoginFrame().setVisible(true);
        });
    }
}