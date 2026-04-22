package com.pulseline.ui.components;

import com.pulseline.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Tabla estilizada reutilizable para todas las pantallas.
 */
public class PulseTable extends JTable {

    public PulseTable(String[] columns) {
        super(new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        setBackground(MainFrame.BG_CARD);
        setForeground(MainFrame.TEXT_PRIMARY);
        setGridColor(MainFrame.BORDER_COLOR);
        setRowHeight(40);
        setShowVerticalLines(false);
        setSelectionBackground(new Color(0x3B82F620, true));
        setSelectionForeground(MainFrame.TEXT_PRIMARY);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setFocusable(false);
        setIntercellSpacing(new Dimension(0, 1));

        // Header
        JTableHeader header = getTableHeader();
        header.setBackground(new Color(0x0F172A));
        header.setForeground(MainFrame.TEXT_MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MainFrame.BORDER_COLOR));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        // Renderer base para alinear y colorear filas alternadas
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? MainFrame.BG_CARD : new Color(0x172033));
                    c.setForeground(MainFrame.TEXT_PRIMARY);
                }
                setBorder(new javax.swing.border.EmptyBorder(0, 12, 0, 12));
                return c;
            }
        };

        for (int i = 0; i < columns.length; i++) {
            getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public DefaultTableModel getModel() {
        return (DefaultTableModel) super.getModel();
    }

    public void clearRows() {
        getModel().setRowCount(0);
    }

    public void addRow(Object[] rowData) {
        getModel().addRow(rowData);
    }

    /**
     * Renderer especial para mostrar chips de estado con colores.
     */
    public static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 11));
            label.setOpaque(true);

            String text = value != null ? value.toString() : "";
            Color bg, fg;
            switch (text.toUpperCase()) {
                case "RESUELTA"  -> { bg = new Color(0x10B98120, true); fg = new Color(0x10B981); }
                case "ESCALADA"  -> { bg = new Color(0xF59E0B20, true); fg = new Color(0xF59E0B); }
                case "CANCELADA" -> { bg = new Color(0xEF444420, true); fg = new Color(0xEF4444); }
                case "PENDIENTE" -> { bg = new Color(0x3B82F620, true); fg = new Color(0x3B82F6); }
                case "JUNIOR"    -> { bg = new Color(0x8B5CF620, true); fg = new Color(0xA78BFA); }
                case "INTERMEDIO"-> { bg = new Color(0x3B82F620, true); fg = new Color(0x60A5FA); }
                case "SENIOR"    -> { bg = new Color(0x10B98120, true); fg = new Color(0x34D399); }
                default          -> { bg = new Color(0x33495E);         fg = MainFrame.TEXT_MUTED; }
            }

            label.setBackground(isSelected ? new Color(0x3B82F640, true) : bg);
            label.setForeground(fg);
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            return label;
        }
    }
}
