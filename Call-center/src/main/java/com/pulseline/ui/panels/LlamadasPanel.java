package com.pulseline.ui.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulseline.ui.MainFrame;
import com.pulseline.ui.SessionContext;
import com.pulseline.ui.components.PulseTable;
import com.pulseline.ui.utils.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LlamadasPanel extends JPanel {

    private PulseTable tabla;
    private final boolean isAdmin = SessionContext.getInstance().isAdmin();

    public LlamadasPanel() {
        setBackground(MainFrame.BG_PANEL);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        loadLlamadas();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Llamadas");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(MainFrame.TEXT_PRIMARY);

        JLabel sub = new JLabel("Registro y seguimiento de llamadas");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(MainFrame.TEXT_MUTED);
        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnRefresh = buildSecondaryButton("↻ Actualizar");
        btnRefresh.addActionListener(e -> loadLlamadas());

        if (isAdmin) {
            JButton btnNueva = buildPrimaryButton("+ Registrar Llamada");
            btnNueva.addActionListener(e -> showFormNueva());
            right.add(btnNueva);
        }
        right.add(btnRefresh);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        tabla = new PulseTable(new String[]{
            "N° Llamada", "Agente", "Cliente", "Duración (s)", "Fecha/Hora", "Estado"
        });
        tabla.getColumnModel().getColumn(5).setCellRenderer(new PulseTable.StatusRenderer());
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);

        if (isAdmin) {
            tabla.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                        String id     = tabla.getValueAt(tabla.getSelectedRow(), 0).toString();
                        String estado = tabla.getValueAt(tabla.getSelectedRow(), 5).toString();
                        showAcciones(id, estado);
                    }
                }
            });
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(MainFrame.BG_CARD);
        scroll.getViewport().setBackground(MainFrame.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_COLOR));

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        bar.setBackground(new Color(0x172033));
        bar.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, MainFrame.BORDER_COLOR));
        bar.add(legendChip("PENDIENTE",  MainFrame.ACCENT_BLUE));
        bar.add(legendChip("RESUELTA",   MainFrame.ACCENT_GREEN));
        bar.add(legendChip("ESCALADA",   MainFrame.ACCENT_ORANGE));
        bar.add(legendChip("CANCELADA",  MainFrame.ACCENT_RED));

        String hint = isAdmin
            ? "  💡 Doble clic para escalar o resolver"
            : "  👁 Solo administradores pueden modificar llamadas";
        JLabel hintLabel = new JLabel(hint);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hintLabel.setForeground(isAdmin ? MainFrame.TEXT_MUTED : MainFrame.ACCENT_ORANGE);
        bar.add(hintLabel);
        return bar;
    }

    private JLabel legendChip(String text, Color color) {
        JLabel l = new JLabel("● " + text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(color);
        return l;
    }

    private void loadLlamadas() {
        SwingWorker<JsonNode, Void> w = new SwingWorker<>() {
            @Override protected JsonNode doInBackground() throws Exception {
                return ApiClient.get("/llamadas");
            }
            @Override protected void done() {
                try {
                    tabla.clearRows();
                    for (JsonNode l : get()) {
                        String fecha = l.path("fechaHora").asText().replace("T", " ");
                        if (fecha.length() > 19) fecha = fecha.substring(0, 19);
                        tabla.addRow(new Object[]{
                            l.path("numeroLlamada").asText(),
                            l.path("idAgente").asText(),
                            l.path("idCliente").asText(),
                            l.path("duracionSegundos").asInt(),
                            fecha,
                            l.path("resultado").asText()
                        });
                    }
                } catch (Exception ex) {
                    showError("No se pudieron cargar las llamadas.\n" + ex.getMessage());
                }
            }
        };
        w.execute();
    }

    private void showFormNueva() {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), "Registrar Llamada", true);
        dialog.setSize(440, 320);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MainFrame.BG_CARD);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField fId      = buildFormField();
        JTextField fAgente  = buildFormField();
        JTextField fCliente = buildFormField();
        JTextField fDurSeg  = buildFormField();
        fDurSeg.setText("0");

        addFormRow(panel, gbc, 0, "N° Llamada",   fId);
        addFormRow(panel, gbc, 1, "ID Agente",     fAgente);
        addFormRow(panel, gbc, 2, "ID Cliente",    fCliente);
        addFormRow(panel, gbc, 3, "Duración (s)",  fDurSeg);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancelar = buildSecondaryButton("Cancelar");
        JButton btnGuardar  = buildPrimaryButton("Registrar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            Map<String, Object> body = new HashMap<>();
            body.put("numeroLlamada", fId.getText().trim());
            body.put("agenteId",      fAgente.getText().trim());
            body.put("clienteId",     fCliente.getText().trim());
            try { body.put("duracion", Integer.parseInt(fDurSeg.getText().trim())); }
            catch (NumberFormatException ex) { body.put("duracion", 0); }

            SwingWorker<Void, Void> w = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiClient.post("/llamadas", body);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        loadLlamadas();
                        showSuccess("Llamada registrada exitosamente.");
                    } catch (Exception ex) { showError(ex.getMessage()); }
                }
            };
            w.execute();
        });
        btns.add(btnCancelar);
        btns.add(btnGuardar);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(btns, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showAcciones(String id, String estado) {
        if ("RESUELTA".equals(estado) || "CANCELADA".equals(estado)) {
            JOptionPane.showMessageDialog(this,
                "La llamada " + id + " ya fue " + estado.toLowerCase() + ".",
                "Sin acciones disponibles", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opciones = {"Resolver", "Escalar", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(this,
            "Selecciona una acción para:\n" + id,
            "Acciones — " + id,
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, opciones, opciones[2]);

        if (opcion == 0) {
            ejecutarAccion(id, "resolver", null);
        } else if (opcion == 1) {
            String motivo = JOptionPane.showInputDialog(this,
                "Motivo de escalación:", "Escalar Llamada",
                JOptionPane.PLAIN_MESSAGE);
            if (motivo != null && !motivo.isBlank()) {
                Map<String, Object> body = new HashMap<>();
                body.put("motivo", motivo);
                ejecutarAccion(id, "escalar", body);
            }
        }
    }

    private void ejecutarAccion(String id, String accion, Object body) {
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            @Override protected Void doInBackground() throws Exception {
                ApiClient.patch("/llamadas/" + id + "/" + accion, body);
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    loadLlamadas();
                    showSuccess("Llamada " + accion + " exitosamente.");
                } catch (Exception ex) { showError(ex.getMessage()); }
            }
        };
        w.execute();
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc,
                             int row, String label, JComponent field) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(MainFrame.TEXT_MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        p.add(field, gbc);
    }

    private JTextField buildFormField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(0x334155));
        f.setForeground(MainFrame.TEXT_PRIMARY);
        f.setCaretColor(MainFrame.TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_COLOR),
            new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JButton buildPrimaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(MainFrame.ACCENT_BLUE);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton buildSecondaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(0x334155));
        b.setForeground(MainFrame.TEXT_PRIMARY);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}