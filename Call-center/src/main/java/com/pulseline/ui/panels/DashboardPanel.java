package com.pulseline.ui.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulseline.ui.MainFrame;
import com.pulseline.ui.components.PulseTable;
import com.pulseline.ui.components.StatCard;
import com.pulseline.ui.utils.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;

/**
 * Panel de Dashboard — vista general del sistema.
 */
public class DashboardPanel extends JPanel {

    private JLabel lblTotalLlamadas, lblResueltas, lblEscaladas, lblAgentes;
    private PulseTable tableLlamadas;
    private JLabel lblLastUpdate;

    public DashboardPanel() {
        setBackground(MainFrame.BG_PANEL);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        // Cargar datos al mostrar
        Timer timer = new Timer(0, this::loadData);
        timer.setRepeats(false);
        timer.start();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(MainFrame.TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        lblLastUpdate = new JLabel("—");
        lblLastUpdate.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblLastUpdate.setForeground(MainFrame.TEXT_MUTED);

        JButton btnRefresh = buildIconButton("↻ Actualizar", MainFrame.ACCENT_BLUE);
        btnRefresh.addActionListener(this::loadData);

        right.add(lblLastUpdate);
        right.add(btnRefresh);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 24));
        body.setOpaque(false);

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        lblTotalLlamadas = new JLabel("—");
        lblResueltas     = new JLabel("—");
        lblEscaladas     = new JLabel("—");
        lblAgentes       = new JLabel("—");

        statsRow.add(makeStatCard("Total Llamadas",  lblTotalLlamadas, "registros en el sistema",  MainFrame.ACCENT_BLUE));
        statsRow.add(makeStatCard("Resueltas",        lblResueltas,     "completadas con éxito",    MainFrame.ACCENT_GREEN));
        statsRow.add(makeStatCard("Escaladas",        lblEscaladas,     "requieren atención",       MainFrame.ACCENT_ORANGE));
        statsRow.add(makeStatCard("Agentes Activos",  lblAgentes,       "disponibles",              MainFrame.ACCENT_BLUE));

        // Table section
        JPanel tableSection = new JPanel(new BorderLayout(0, 12));
        tableSection.setOpaque(false);

        JLabel tableTitle = new JLabel("Llamadas Recientes");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        tableTitle.setForeground(MainFrame.TEXT_PRIMARY);

        tableLlamadas = new PulseTable(new String[]{"#", "N° Llamada", "Agente", "Cliente", "Duración", "Estado"});
        tableLlamadas.getColumnModel().getColumn(5).setCellRenderer(new PulseTable.StatusRenderer());
        tableLlamadas.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(tableLlamadas);
        scroll.setBackground(MainFrame.BG_CARD);
        scroll.getViewport().setBackground(MainFrame.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_COLOR));

        tableSection.add(tableTitle, BorderLayout.NORTH);
        tableSection.add(scroll, BorderLayout.CENTER);

        body.add(statsRow, BorderLayout.NORTH);
        body.add(tableSection, BorderLayout.CENTER);
        return body;
    }

    private JPanel makeStatCard(String title, JLabel valueLabel, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_COLOR),
            new EmptyBorder(20, 24, 20, 24)
        ));

        // Top accent bar
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 36, 4, 4, 4);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 12));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(MainFrame.TEXT_PRIMARY);
        valueLabel.setBorder(new EmptyBorder(6, 0, 4, 0));

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

        card.add(bar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private void loadData(ActionEvent e) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int totalLlamadas = 0, resueltas = 0, escaladas = 0, totalAgentes = 0;
            JsonNode llamadas, agentes;

            @Override
            protected Void doInBackground() throws Exception {
                llamadas  = ApiClient.get("/llamadas");
                agentes   = ApiClient.get("/agentes");
                if (llamadas.isArray()) {
                    totalLlamadas = llamadas.size();
                    for (JsonNode l : llamadas) {
                        String res = l.path("resultado").asText();
                        if ("RESUELTA".equals(res))  resueltas++;
                        if ("ESCALADA".equals(res))  escaladas++;
                    }
                }
                if (agentes.isArray()) totalAgentes = agentes.size();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // lanza excepción si falló
                    lblTotalLlamadas.setText(String.valueOf(totalLlamadas));
                    lblResueltas.setText(String.valueOf(resueltas));
                    lblEscaladas.setText(String.valueOf(escaladas));
                    lblAgentes.setText(String.valueOf(totalAgentes));

                    tableLlamadas.clearRows();
                    int i = 1;
                    for (JsonNode l : llamadas) {
                        String dur = l.path("duracionSegundos").asInt() + "s";
                        tableLlamadas.addRow(new Object[]{
                            i++,
                            l.path("numeroLlamada").asText(),
                            l.path("idAgente").asText(),
                            l.path("idCliente").asText(),
                            dur,
                            l.path("resultado").asText()
                        });
                    }
                    lblLastUpdate.setText("Actualizado: " +
                        java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                } catch (Exception ex) {
                    lblTotalLlamadas.setText("N/A");
                    lblLastUpdate.setText("⚠ Sin conexión con API");
                }
            }
        };
        worker.execute();
    }

    private JButton buildIconButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
