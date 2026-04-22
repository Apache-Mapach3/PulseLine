package com.pulseline.ui.panels;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulseline.ui.MainFrame;
import com.pulseline.ui.components.PulseTable;
import com.pulseline.ui.utils.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel de gestión de Agentes — listado, registro y eliminación.
 */
public class AgentesPanel extends JPanel {

    private PulseTable tabla;
    private JTextField txtBuscar;

    public AgentesPanel() {
        setBackground(MainFrame.BG_PANEL);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);

        loadAgentes();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Título + subtítulo
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        JLabel title = new JLabel("Agentes");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(MainFrame.TEXT_PRIMARY);
        JLabel sub = new JLabel("Gestión del equipo de agentes");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(MainFrame.TEXT_MUTED);
        left.add(title);
        left.add(sub);

        // Acciones
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        txtBuscar = buildTextField("Buscar agente...", 200);

        JButton btnNuevo    = buildPrimaryButton("+ Nuevo Agente");
        JButton btnRefresh  = buildSecondaryButton("↻");
        btnNuevo.addActionListener(e -> showFormNuevoAgente());
        btnRefresh.addActionListener(e -> loadAgentes());

        right.add(txtBuscar);
        right.add(btnNuevo);
        right.add(btnRefresh);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        tabla = new PulseTable(new String[]{"ID Agente", "Nombre Completo", "N° Empleado", "Email", "Nivel", "Campañas"});
        tabla.getColumnModel().getColumn(4).setCellRenderer(new PulseTable.StatusRenderer());
        tabla.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(70);

        // Doble click → detalle
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    String id = tabla.getValueAt(tabla.getSelectedRow(), 0).toString();
                    showAcciones(id);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(MainFrame.BG_CARD);
        scroll.getViewport().setBackground(MainFrame.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_COLOR));

        // Barra inferior de acciones
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionBar.setBackground(new Color(0x172033));
        actionBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.BORDER_COLOR));

        JLabel hint = new JLabel("💡 Doble clic en una fila para ver acciones");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(MainFrame.TEXT_MUTED);
        actionBar.add(hint);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(actionBar, BorderLayout.SOUTH);
        return panel;
    }

    // ── DATA LOADING ─────────────────────────────────────────────────────────

    private void loadAgentes() {
        SwingWorker<JsonNode, Void> worker = new SwingWorker<>() {
            @Override protected JsonNode doInBackground() throws Exception {
                return ApiClient.get("/agentes");
            }
            @Override protected void done() {
                try {
                    JsonNode agentes = get();
                    tabla.clearRows();
                    for (JsonNode a : agentes) {
                        int campanias = a.path("campaniasAsignadas").size();
                        tabla.addRow(new Object[]{
                            a.path("idAgente").asText(),
                            a.path("nombreCompleto").asText(),
                            a.path("numeroEmpleado").asText("-"),
                            a.path("email").asText("-"),
                            a.path("nivelExperiencia").asText(),
                            campanias + " asig."
                        });
                    }
                } catch (Exception ex) {
                    showError("No se pudo cargar la lista de agentes.\n" + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── FORMULARIO NUEVO AGENTE ───────────────────────────────────────────────

    private void showFormNuevoAgente() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Agente", true);
        dialog.setSize(460, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(MainFrame.BG_CARD);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MainFrame.BG_CARD);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField fId       = buildFormField();
        JTextField fNombre   = buildFormField();
        JTextField fEmpleado = buildFormField();
        JTextField fEmail    = buildFormField();
        String[]   niveles   = {"JUNIOR", "INTERMEDIO", "SENIOR"};
        JComboBox<String> fNivel = new JComboBox<>(niveles);
        fNivel.setBackground(new Color(0x334155));
        fNivel.setForeground(MainFrame.TEXT_PRIMARY);

        addFormRow(panel, gbc, 0, "ID Agente",       fId);
        addFormRow(panel, gbc, 1, "Nombre Completo", fNombre);
        addFormRow(panel, gbc, 2, "N° Empleado",     fEmpleado);
        addFormRow(panel, gbc, 3, "Email",            fEmail);
        addFormRow(panel, gbc, 4, "Nivel",            fNivel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton btnCancel = buildSecondaryButton("Cancelar");
        JButton btnGuardar = buildPrimaryButton("Guardar");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            Map<String, Object> body = new HashMap<>();
            body.put("idAgente",       fId.getText().trim());
            body.put("nombreCompleto", fNombre.getText().trim());
            body.put("numeroEmpleado", fEmpleado.getText().trim());
            body.put("email",          fEmail.getText().trim());
            body.put("nivelExperiencia", fNivel.getSelectedItem());

            SwingWorker<Void, Void> w = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiClient.post("/agentes", body);
                    return null;
                }
                @Override protected void done() {
                    try { get(); dialog.dispose(); loadAgentes();
                        showSuccess("Agente registrado exitosamente.");
                    } catch (Exception ex) { showError(ex.getMessage()); }
                }
            };
            w.execute();
        });
        buttons.add(btnCancel);
        buttons.add(btnGuardar);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(buttons, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showAcciones(String idAgente) {
        int opcion = JOptionPane.showOptionDialog(this,
            "Agente: " + idAgente,
            "Acciones",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new String[]{"Eliminar", "Cancelar"},
            "Cancelar"
        );
        if (opcion == 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el agente " + idAgente + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SwingWorker<Void, Void> w = new SwingWorker<>() {
                    @Override protected Void doInBackground() throws Exception {
                        ApiClient.delete("/agentes/" + idAgente);
                        return null;
                    }
                    @Override protected void done() {
                        try { get(); loadAgentes(); showSuccess("Agente eliminado."); }
                        catch (Exception ex) { showError(ex.getMessage()); }
                    }
                };
                w.execute();
            }
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(MainFrame.TEXT_MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(field, gbc);
    }

    private JTextField buildFormField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(0x334155));
        f.setForeground(MainFrame.TEXT_PRIMARY);
        f.setCaretColor(MainFrame.TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_COLOR),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private JTextField buildTextField(String placeholder, int width) {
        JTextField f = new JTextField(placeholder);
        f.setForeground(MainFrame.TEXT_MUTED);
        f.setBackground(new Color(0x1E293B));
        f.setCaretColor(MainFrame.TEXT_PRIMARY);
        f.setPreferredSize(new Dimension(width, 32));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_COLOR),
            new EmptyBorder(4, 10, 4, 10)
        ));
        return f;
    }

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

    private JButton buildSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0x334155));
        b.setForeground(MainFrame.TEXT_PRIMARY);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
