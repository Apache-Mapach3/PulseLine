package com.pulseline.ui.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulseline.ui.MainFrame;
import com.pulseline.ui.SessionContext;
import com.pulseline.ui.components.PulseTable;
import com.pulseline.ui.utils.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private JLabel lblTotalLlamadas, lblResueltas, lblEscaladas, lblAgentes;
    private PulseTable tableLlamadas;
    private JLabel lblLastUpdate;
    private DonutChartPanel donutChart;
    private BarChartPanel barChart;

    // Datos para gráficas
    private int totalLlamadas = 0, resueltas = 0, escaladas = 0,
                canceladas = 0, pendientes = 0;

    public DashboardPanel() {
        setBackground(MainFrame.BG_PANEL);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 32, 28, 32));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);

        javax.swing.Timer timer = new javax.swing.Timer(0, this::loadData);
        timer.setRepeats(false);
        timer.start();
            }

    // ── HEADER ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        // Título + saludo
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(MainFrame.TEXT_PRIMARY);

        String nombre = SessionContext.getInstance().getNombreUsuario();
        JLabel greeting = new JLabel("Bienvenido, " + nombre + " 👋");
        greeting.setFont(new Font("SansSerif", Font.PLAIN, 13));
        greeting.setForeground(MainFrame.TEXT_MUTED);

        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(greeting);

        // Botón + última actualización
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        lblLastUpdate = new JLabel("—");
        lblLastUpdate.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblLastUpdate.setForeground(MainFrame.TEXT_MUTED);

        JButton btnRefresh = buildPrimaryButton("↻  Actualizar");
        btnRefresh.addActionListener(this::loadData);

        right.add(lblLastUpdate);
        right.add(btnRefresh);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── BODY ──────────────────────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);

        body.add(buildStatsRow(),   BorderLayout.NORTH);
        body.add(buildMiddleRow(),  BorderLayout.CENTER);
        return body;
    }

    // ── STAT CARDS ────────────────────────────────────────────────────────────

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        lblTotalLlamadas = new JLabel("—");
        lblResueltas     = new JLabel("—");
        lblEscaladas     = new JLabel("—");
        lblAgentes       = new JLabel("—");

        row.add(buildStatCard("Total Llamadas",  lblTotalLlamadas,
            "registros totales",   MainFrame.ACCENT_BLUE,   "📞"));
        row.add(buildStatCard("Resueltas",        lblResueltas,
            "completadas con éxito", MainFrame.ACCENT_GREEN, "✅"));
        row.add(buildStatCard("Escaladas",        lblEscaladas,
            "requieren atención",  MainFrame.ACCENT_ORANGE, "⚠"));
        row.add(buildStatCard("Agentes Activos",  lblAgentes,
            "en el sistema",       MainFrame.ACCENT_BLUE,   "👤"));
        return row;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel,
                                  String subtitle, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo con gradiente sutil
                GradientPaint gp = new GradientPaint(
                    0, 0, MainFrame.BG_CARD,
                    0, getHeight(), new Color(
                        Math.min(MainFrame.BG_CARD.getRed()   + 8, 255),
                        Math.min(MainFrame.BG_CARD.getGreen() + 8, 255),
                        Math.min(MainFrame.BG_CARD.getBlue()  + 15, 255)
                    )
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(accent.getRed(), accent.getGreen(),
                          accent.getBlue(), 60), 1),
            new EmptyBorder(18, 22, 18, 22)
        ));

        // Barra superior de acento
        JPanel topBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, accent,
                    50, 0, accent.darker()
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, 44, 4, 4, 4);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 10));

        // Ícono + valor en la misma fila
        JPanel midRow = new JPanel(new BorderLayout());
        midRow.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
        valueLabel.setForeground(MainFrame.TEXT_PRIMARY);
        valueLabel.setBorder(new EmptyBorder(4, 0, 2, 0));

        midRow.add(valueLabel,  BorderLayout.WEST);
        midRow.add(iconLabel,   BorderLayout.EAST);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setForeground(MainFrame.TEXT_PRIMARY);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("SansSerif", Font.PLAIN, 11));
        s.setForeground(MainFrame.TEXT_MUTED);

        bottom.add(t);
        bottom.add(Box.createVerticalStrut(2));
        bottom.add(s);

        card.add(topBar,  BorderLayout.NORTH);
        card.add(midRow,  BorderLayout.CENTER);
        card.add(bottom,  BorderLayout.SOUTH);
        return card;
    }

    // ── FILA CENTRAL (tabla + gráficas) ──────────────────────────────────────

    private JPanel buildMiddleRow() {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);

        row.add(buildTableSection(), BorderLayout.CENTER);
        row.add(buildChartsPanel(),  BorderLayout.EAST);
        return row;
    }

    private JPanel buildTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);

        JLabel tableTitle = new JLabel("Llamadas Recientes");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        tableTitle.setForeground(MainFrame.TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 6, 0));

        tableLlamadas = new PulseTable(
            new String[]{"#", "N° Llamada", "Agente", "Cliente", "Duración", "Estado"});
        tableLlamadas.getColumnModel().getColumn(5)
            .setCellRenderer(new PulseTable.StatusRenderer());
        tableLlamadas.getColumnModel().getColumn(0).setMaxWidth(45);
        tableLlamadas.getColumnModel().getColumn(4).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(tableLlamadas);
        scroll.setBackground(MainFrame.BG_CARD);
        scroll.getViewport().setBackground(MainFrame.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_COLOR));

        section.add(tableTitle, BorderLayout.NORTH);
        section.add(scroll,     BorderLayout.CENTER);
        return section;
    }

    private JPanel buildChartsPanel() {
        JPanel charts = new JPanel();
        charts.setLayout(new BoxLayout(charts, BoxLayout.Y_AXIS));
        charts.setOpaque(false);
        charts.setPreferredSize(new Dimension(260, 0));

        // Donut chart — distribución de estados
        JLabel donutTitle = new JLabel("Distribución de Estados");
        donutTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        donutTitle.setForeground(MainFrame.TEXT_PRIMARY);
        donutTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        donutChart = new DonutChartPanel();
        donutChart.setAlignmentX(Component.LEFT_ALIGNMENT);
        donutChart.setMaximumSize(new Dimension(260, 190));
        donutChart.setPreferredSize(new Dimension(260, 190));

        // Bar chart — agentes vs llamadas
        JLabel barTitle = new JLabel("Actividad por Estado");
        barTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        barTitle.setForeground(MainFrame.TEXT_PRIMARY);
        barTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        barTitle.setBorder(new EmptyBorder(14, 0, 6, 0));

        barChart = new BarChartPanel();
        barChart.setAlignmentX(Component.LEFT_ALIGNMENT);
        barChart.setMaximumSize(new Dimension(260, 160));
        barChart.setPreferredSize(new Dimension(260, 160));

        charts.add(donutTitle);
        charts.add(Box.createVerticalStrut(6));
        charts.add(donutChart);
        charts.add(barTitle);
        charts.add(barChart);
        return charts;
    }

    // ── DATA LOADING ─────────────────────────────────────────────────────────

    private void loadData(ActionEvent e) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            JsonNode llamadas, agentes;

            @Override protected Void doInBackground() throws Exception {
                llamadas = ApiClient.get("/llamadas");
                agentes  = ApiClient.get("/agentes");
                return null;
            }

            @Override protected void done() {
                try {
                    get();
                    // Reset contadores
                    totalLlamadas = 0; resueltas = 0; escaladas = 0;
                    canceladas = 0; pendientes = 0;

                    if (llamadas.isArray()) {
                        totalLlamadas = llamadas.size();
                        for (JsonNode l : llamadas) {
                            switch (l.path("resultado").asText()) {
                                case "RESUELTA"  -> resueltas++;
                                case "ESCALADA"  -> escaladas++;
                                case "CANCELADA" -> canceladas++;
                                case "PENDIENTE" -> pendientes++;
                            }
                        }
                    }

                    int totalAgentes = agentes.isArray() ? agentes.size() : 0;

                    // Actualizar stat cards
                    lblTotalLlamadas.setText(String.valueOf(totalLlamadas));
                    lblResueltas.setText(String.valueOf(resueltas));
                    lblEscaladas.setText(String.valueOf(escaladas));
                    lblAgentes.setText(String.valueOf(totalAgentes));

                    // Actualizar tabla
                    tableLlamadas.clearRows();
                    int i = 1;
                    for (JsonNode l : llamadas) {
                        tableLlamadas.addRow(new Object[]{
                            i++,
                            l.path("numeroLlamada").asText(),
                            l.path("idAgente").asText(),
                            l.path("idCliente").asText(),
                            l.path("duracionSegundos").asInt() + "s",
                            l.path("resultado").asText()
                        });
                    }

                    // Actualizar gráficas
                    donutChart.updateData(resueltas, escaladas, canceladas, pendientes);
                    barChart.updateData(resueltas, escaladas, canceladas, pendientes);

                    lblLastUpdate.setText("Actualizado: " +
                        java.time.LocalTime.now()
                            .format(DateTimeFormatter.ofPattern("HH:mm:ss")));

                } catch (Exception ex) {
                    lblTotalLlamadas.setText("N/A");
                    lblLastUpdate.setText("⚠  Sin conexión con API");
                }
            }
        };
        worker.execute();
    }

    // ── GRÁFICA DONUT ────────────────────────────────────────────────────────

    static class DonutChartPanel extends JPanel {
        private int resuelta, escalada, cancelada, pendiente;

        DonutChartPanel() {
            setOpaque(false);
            setBackground(new Color(0, 0, 0, 0));
        }

        void updateData(int r, int e, int c, int p) {
            this.resuelta  = r; this.escalada  = e;
            this.cancelada = c; this.pendiente = p;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            int total = resuelta + escalada + cancelada + pendiente;
            int size  = Math.min(getWidth(), getHeight() - 40);
            int x     = (getWidth() - size) / 2;
            int y     = 10;
            int hole  = (int)(size * 0.52);
            int hx    = x + (size - hole) / 2;
            int hy    = y + (size - hole) / 2;

            if (total == 0) {
                g2.setColor(MainFrame.BORDER_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x, y, size, size);
                g2.setColor(MainFrame.TEXT_MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Sin datos", x + size/2 - 28, y + size/2 + 4);
                g2.dispose();
                return;
            }

            int[]    values = {resuelta, escalada, cancelada, pendiente};
            Color[]  colors = {
                MainFrame.ACCENT_GREEN,
                MainFrame.ACCENT_ORANGE,
                MainFrame.ACCENT_RED,
                MainFrame.ACCENT_BLUE
            };
            String[] labels = {"Res.", "Esc.", "Can.", "Pen."};

            double startAngle = 90;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == 0) continue;
                double sweep = 360.0 * values[i] / total;
                g2.setColor(colors[i]);
                g2.fill(new Arc2D.Double(x, y, size, size,
                    startAngle, sweep, Arc2D.PIE));
                startAngle += sweep;
            }

            // Hueco central
            g2.setColor(MainFrame.BG_PANEL);
            g2.fillOval(hx, hy, hole, hole);

            // Texto central
            g2.setColor(MainFrame.TEXT_PRIMARY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            String totalStr = String.valueOf(total);
            FontMetrics fm  = g2.getFontMetrics();
            g2.drawString(totalStr,
                hx + (hole - fm.stringWidth(totalStr)) / 2,
                hy + hole/2 + fm.getAscent()/2 - 6);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(MainFrame.TEXT_MUTED);
            g2.drawString("total",
                hx + (hole - g2.getFontMetrics().stringWidth("total")) / 2,
                hy + hole/2 + fm.getAscent()/2 + 10);

            // Leyenda inferior
            int lx = 4, ly = y + size + 12;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int i = 0; i < values.length; i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(lx, ly, 10, 10, 3, 3);
                g2.setColor(MainFrame.TEXT_MUTED);
                String lbl = labels[i] + " " + values[i];
                g2.drawString(lbl, lx + 14, ly + 9);
                lx += g2.getFontMetrics().stringWidth(lbl) + 24;
            }
            g2.dispose();
        }
    }

    // ── GRÁFICA DE BARRAS ────────────────────────────────────────────────────

    static class BarChartPanel extends JPanel {
        private int resuelta, escalada, cancelada, pendiente;

        BarChartPanel() { setOpaque(false); }

        void updateData(int r, int e, int c, int p) {
            this.resuelta = r; this.escalada = e;
            this.cancelada = c; this.pendiente = p;
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            int[]    vals   = {resuelta, escalada, cancelada, pendiente};
            Color[]  colors = {
                MainFrame.ACCENT_GREEN,
                MainFrame.ACCENT_ORANGE,
                MainFrame.ACCENT_RED,
                MainFrame.ACCENT_BLUE
            };
            String[] labels = {"Resueltas", "Escaladas", "Canceladas", "Pendientes"};

            int max   = Arrays.stream(vals).max().orElse(1);
            if (max == 0) max = 1;

            int barW   = (getWidth() - 40) / vals.length - 12;
            int chartH = getHeight() - 50;
            int baseY  = chartH + 8;

            for (int i = 0; i < vals.length; i++) {
                int barH = (int)((double) vals[i] / max * chartH);
                int bx   = 20 + i * (barW + 12);
                int by   = baseY - barH;

                // Barra con gradiente
                GradientPaint gp = new GradientPaint(
                    bx, by, colors[i],
                    bx, baseY, colors[i].darker()
                );
                g2.setPaint(gp);
                g2.fillRoundRect(bx, by, barW, barH + 4, 6, 6);

                // Valor encima
                g2.setColor(MainFrame.TEXT_PRIMARY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String val = String.valueOf(vals[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(val, bx + (barW - fm.stringWidth(val)) / 2, by - 4);

                // Etiqueta debajo
                g2.setColor(MainFrame.TEXT_MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(labels[i],
                    bx + (barW - fm2.stringWidth(labels[i])) / 2,
                    baseY + 16);
            }

            // Línea base
            g2.setColor(MainFrame.BORDER_COLOR);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(10, baseY + 2, getWidth() - 10, baseY + 2);

            g2.dispose();
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private JButton buildPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(MainFrame.ACCENT_BLUE);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}