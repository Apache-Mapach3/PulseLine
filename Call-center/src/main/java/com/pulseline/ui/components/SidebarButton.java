package com.pulseline.ui.components;

import com.pulseline.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botón de navegación del sidebar con estado activo/hover.
 */
public class SidebarButton extends JButton {

    private boolean active = false;
    private boolean hovered = false;

    public SidebarButton(String icon, String label) {
        super();
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 0));
        setMaximumSize(new Dimension(230, 44));
        setPreferredSize(new Dimension(230, 44));
        setMinimumSize(new Dimension(230, 44));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLabel.setForeground(MainFrame.TEXT_MUTED);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textLabel.setForeground(MainFrame.TEXT_MUTED);

        add(iconLabel);
        add(textLabel);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        // Actualizar color de los labels hijos
        for (Component c : getComponents()) {
            if (c instanceof JLabel lbl) {
                lbl.setForeground(active ? MainFrame.TEXT_PRIMARY : MainFrame.TEXT_MUTED);
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (active) {
            // Fondo azul semitransparente + borde izquierdo
            g2.setColor(new Color(0x3B82F6, false).darker());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(MainFrame.ACCENT_BLUE);
            g2.fillRect(0, 0, 3, getHeight());
        } else if (hovered) {
            g2.setColor(new Color(0xFF334155, true));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
