package com.pulseline.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada de la interfaz gráfica de escritorio.
 * Se lanza DESPUÉS de que Spring Boot haya iniciado el servidor embebido.
 * La UI consume la API REST en localhost:8080.
 */
public class PulseLineUI {

    public static void launch() {
        // Configurar FlatLaf ANTES de crear cualquier componente Swing
        setupLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static void setupLookAndFeel() {
        // Propiedades globales de FlatLaf para un look más pulido
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ProgressBar.arc", 8);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("Table.rowHeight", 36);
        UIManager.put("TableHeader.height", 40);

        // Colores corporativos de PulseLine
        UIManager.put("Component.focusColor", new Color(0x3B82F6));
        UIManager.put("Component.focusedBorderColor", new Color(0x3B82F6));
        UIManager.put("Button.default.background", new Color(0x3B82F6));
        UIManager.put("Button.default.foreground", Color.WHITE);
        UIManager.put("Button.default.hoverBackground", new Color(0x2563EB));

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            FlatLaf.updateUI();
        } catch (Exception e) {
            System.err.println("No se pudo cargar FlatDarkLaf: " + e.getMessage());
        }
    }
}
