package com.pulseline.ui.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulseline.ui.MainFrame;
import com.pulseline.ui.components.PulseTable;
import com.pulseline.ui.utils.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel de gestión de Campañas.
 */
public class CampaniasPanel extends JPanel {

    private PulseTable tabla;

    public CampaniasPanel() {
        setBackground(MainFrame.BG_PANEL);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        loadCampanias();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        JLabel title = new JLabel("Campañas");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(MainFrame.TEXT_PRIMARY);
        JLabel sub = new JLabel("Gestión de campañas de atención");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(MainFrame.TEXT_MUTED);
        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JButton btnNueva   = buildPrimaryButton("+ Nueva Campaña");
        JButton btnAsignar = buildSecondaryButton("Asignar Agente");
        JButton btnRefresh = buildSecondaryButton("↻");
        btnNueva.addActionListener(e -> showFormNueva());
        btnAsignar.addActionListener(e -> showFormAsignar());
        btnRefresh.addActionListener(e -> loadCampanias());
        right.add(btnAsignar);
        right.add(btnNueva);
        right.add(btnRefresh);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        tabla = new PulseTable(new String[]{"ID Campaña", "Nombre", "Tipo", "Fecha Inicio", "Fecha Fin"});
        tabla.getColumnModel().getColumn(2).setCellRenderer(new PulseTable.StatusRenderer());
        tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(MainFrame.BG_CARD);
        scroll.getViewport().setBackground(MainFrame.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_COLOR));

        // Tipos de campaña - leyenda de colores
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        legend.setBackground(new Color(0x172033));
        legend.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.BORDER_COLOR));
        for (String tipo : new String[]{"VENTAS", "SOPORTE_TECNICO", "COBRANZAS", "ENCUESTAS"}) {
            JLabel l = new JLabel("■ " + tipo);
            l.setFont(new Font("SansSerif", Font.PLAIN, 11));
            l.setForeground(MainFrame.TEXT_MUTED);
            legend.add(l);
        }

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(legend, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCampanias() {
        SwingWorker<JsonNode, Void> w = new SwingWorker<>() {
            @Override protected JsonNode doInBackground() throws Exception {
                return ApiClient.get("/campanias");
            }
            @Override protected void done() {
                try {
                    tabla.clearRows();
                    for (JsonNode c : get()) {
                        tabla.addRow(new Object[]{
                            c.path("idCampania").asText(),
                            c.path("nombre").asText(),
                            c.path("tipo").asText(),
                            c.path("fechaInicio").asText(),
                            c.path("fechaFin").asText()
                        });
                    }
                } catch (Exception ex) {
                    showError("No se pudieron cargar las campañas.\n" + ex.getMessage());
                }
            }
        };
        w.execute();
    }

    private void showFormNueva() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Campaña", true);
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MainFrame.BG_CARD);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField fId     = buildFormField();
        JTextField fNombre = buildFormField();
        JComboBox<String> fTipo = new JComboBox<>(new String[]{"VENTAS", "SOPORTE_TECNICO", "COBRANZAS", "ENCUESTAS"});
        fTipo.setBackground(new Color(0x334155));
        fTipo.setForeground(MainFrame.TEXT_PRIMARY);
        JTextField fInicio = buildFormField(); fInicio.setText("2024-07-01");
        JTextField fFin    = buildFormField(); fFin.setText("2024-12-31");

        addFormRow(panel, gbc, 0, "ID Campaña",   fId);
        addFormRow(panel, gbc, 1, "Nombre",        fNombre);
        addFormRow(panel, gbc, 2, "Tipo",          fTipo);
        addFormRow(panel, gbc, 3, "Fecha Inicio",  fInicio);
        addFormRow(panel, gbc, 4, "Fecha Fin",     fFin);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton btnCancelar = buildSecondaryButton("Cancelar");
        JButton btnGuardar  = buildPrimaryButton("Crear");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            Map<String, Object> body = new HashMap<>();
            body.put("idCampania",  fId.getText().trim());
            body.put("nombre",      fNombre.getText().trim());
            body.put("tipo",        fTipo.getSelectedItem());
            body.put("fechaInicio", fInicio.getText().trim());
            body.put("fechaFin",    fFin.getText().trim());

            SwingWorker<Void, Void> sw = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiClient.post("/campanias", body);
                    return null;
                }
                @Override protected void done() {
                    try { get(); dialog.dispose(); loadCampanias(); showSuccess("Campaña creada."); }
                    catch (Exception ex) { showError(ex.getMessage()); }
                }
            };
            sw.execute();
        });
        btns.add(btnCancelar);
        btns.add(btnGuardar);
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(btns, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showFormAsignar() {
        JTextField fAgente   = buildFormField();
        JTextField fCampania = buildFormField();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MainFrame.BG_CARD);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        addFormRow(panel, gbc, 0, "ID Agente",   fAgente);
        addFormRow(panel, gbc, 1, "ID Campaña",  fCampania);

        int result = JOptionPane.showConfirmDialog(this, panel, "Asignar Agente a Campaña",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Map<String, Object> body = new HashMap<>();
            body.put("idAgente", fAgente.getText().trim());

            SwingWorker<Void, Void> w = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiClient.post("/campanias/" + fCampania.getText().trim() + "/asignar", body);
                    return null;
                }
                @Override protected void done() {
                    try { get(); showSuccess("Agente asignado a la campaña exitosamente."); }
                    catch (Exception ex) { showError(ex.getMessage()); }
                }
            };
            w.execute();
        }
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
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
        b.setBackground(MainFrame.ACCENT_BLUE); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton buildSecondaryButton(String t) {
        JButton b = new JButton(t);
        b.setBackground(new Color(0x334155)); b.setForeground(MainFrame.TEXT_PRIMARY);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
