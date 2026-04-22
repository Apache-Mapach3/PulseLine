package com.pulseline.ui.components;

import com.pulseline.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tarjeta de métrica reutilizable para el Dashboard.
 * Muestra un valor numérico grande, una etiqueta y un color de acento.
 */
public class StatCard extends JPanel {

    public StatCard(String title, String value, String subtitle, Color accentColor) {
        setBackground(MainFrame.BG_CARD);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Barra de color superior
        JPanel topBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 40, 4, 4, 4);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 12));

        // Número grande
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 38));
        valueLabel.setForeground(MainFrame.TEXT_PRIMARY);
        valueLabel.setBorder(new EmptyBorder(8, 0, 2, 0));

        // Título
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setForeground(MainFrame.TEXT_PRIMARY);

        // Subtítulo / descripción
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLabel.setForeground(MainFrame.TEXT_MUTED);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);
        bottom.add(titleLabel);
        bottom.add(Box.createVerticalStrut(2));
        bottom.add(subLabel);

        add(topBar, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // Borde redondeado
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_COLOR, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));
    }
}
