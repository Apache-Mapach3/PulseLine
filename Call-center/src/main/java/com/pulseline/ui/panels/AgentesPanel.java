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

public class AgentesPanel extends JPanel {

    private PulseTable tabla;
    private JTextField txtBuscar;
    private final boolean isAdmin = SessionContext.getInstance().isAdmin();

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

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Agentes");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(MainFrame.TEXT_PRIMARY);

        JLabel sub = new JLabel("Gestión del equipo de agentes");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(MainFrame.TEXT_MUTED);

        // Badge de modo lectura si es AGENTE
        if (!isAdmin) {
            JLabel badge = new JLabel(" 👁 Modo lectura ");
            badge.setFont(new Font("SansSerif", Font.BOLD, 10));
            badge.setForeground(MainFrame.ACCENT_ORANGE);
            badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.ACCENT_ORANGE, 1),
                new EmptyBorder(2, 6, 2, 6)
            ));
            left.add(title);
            left.add(Box.createVerticalStrut(4));
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            row.setOpaque(false);
            row.add(sub);
            row.add(Box.createHorizontalStrut(8));
            row.add(badge);
            left.add(row);
        } else {
            left.add(title);
            left.add(sub);
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        txtBuscar = buildTextField("Buscar agente...", 200);
        JButton btnRefresh = buildSecondaryButton("↻ Actualizar");
        btnRefresh.addActionListener(e -> loadAgentes());
        right.add(txtBuscar);

        if (isAdmin) {
            JButton btnNuevo = buildPrimaryButton("+ Nuevo Agente");
            btnNuevo.addActionListener(e -> showFormNuevoAgente());
            right.add(btnNuevo);

            // Botón para agregar usuario agente al workspace
            JButton btnAgregarUsuario = buildSecondaryButton("+ Usuario Agente");
            btnAgregarUsuario.addActionListener(e -> showFormAgregarUsuario());
            right.add(btnAgregarUsuario);
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
            "ID Agente", "Nombre Completo", "N° Empleado", "Email", "Nivel", "Campañas"
        });
        tabla.getColumnModel().getColumn(4).setCellRenderer(new PulseTable.StatusRenderer());
        tabla.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(70);

        if (isAdmin) {
            tabla.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                        String id = tabla.getValueAt(tabla.getSelectedRow(), 0).toString();
                        showAcciones(id);
                    }
                }
            });
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(MainFrame.BG_CARD);
        scroll.getViewport().setBackground(MainFrame.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_COLOR));

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionBar.setBackground(new Color(0x172033));
        actionBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MainFrame.BORDER_COLOR));

        String hintText = isAdmin
            ? "💡 Doble clic en una fila para editar o eliminar"
            : "👁  Solo administradores pueden modificar agentes";
        JLabel hint = new JLabel(hintText);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(isAdmin ? MainFrame.TEXT_MUTED : MainFrame.ACCENT_ORANGE);
        actionBar.add(hint);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(actionBar, BorderLayout.SOUTH);
        return panel;
    }

    // ── DATA ─────────────────────────────────────────────────────────────────

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

    // ── FORMULARIOS ──────────────────────────────────────────────────────────

    private void showFormNuevoAgente() {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Agente", true);
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
        JComboBox<String> fNivel = new JComboBox<>(
            new String[]{"JUNIOR", "INTERMEDIO", "SENIOR"});
        fNivel.setBackground(new Color(0x334155));
        fNivel.setForeground(MainFrame.TEXT_PRIMARY);

        addFormRow(panel, gbc, 0, "ID Agente",       fId);
        addFormRow(panel, gbc, 1, "Nombre Completo", fNombre);
        addFormRow(panel, gbc, 2, "N° Empleado",     fEmpleado);
        addFormRow(panel, gbc, 3, "Email",            fEmail);
        addFormRow(panel, gbc, 4, "Nivel",            fNivel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton btnCancel  = buildSecondaryButton("Cancelar");
        JButton btnGuardar = buildPrimaryButton("Guardar");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            Map<String, Object> body = new HashMap<>();
            body.put("idAgente",         fId.getText().trim());
            body.put("nombreCompleto",   fNombre.getText().trim());
            body.put("numeroEmpleado",   fEmpleado.getText().trim());
            body.put("email",            fEmail.getText().trim());
            body.put("nivelExperiencia", fNivel.getSelectedItem());

            SwingWorker<Void, Void> w = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiClient.post("/agentes", body);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        loadAgentes();
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

    private void showFormAgregarUsuario() {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), "Agregar Usuario Agente", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MainFrame.BG_CARD);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField fUsername   = buildFormField();
        JPasswordField fPass   = new JPasswordField();
        fPass.setBackground(new Color(0x334155));
        fPass.setForeground(MainFrame.TEXT_PRIMARY);
        fPass.setCaretColor(MainFrame.TEXT_PRIMARY);
        fPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.BORDER_COLOR),
            new EmptyBorder(6, 10, 6, 10)));

        addFormRow(panel, gbc, 0, "Nombre de usuario", fUsername);
        addFormRow(panel, gbc, 1, "Contraseña",        fPass);

        JLabel infoLabel = new JLabel(
            "<html><small>Este usuario podrá acceder al workspace en modo<br>lectura con el código: <b>" +
            SessionContext.getInstance().getWorkspaceId() + "</b></small></html>");
        infoLabel.setForeground(MainFrame.TEXT_MUTED);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(infoLabel, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton btnCancel  = buildSecondaryButton("Cancelar");
        JButton btnGuardar = buildPrimaryButton("Crear Usuario");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            Map<String, Object> body = new HashMap<>();
            body.put("username",    fUsername.getText().trim());
            body.put("password",    new String(fPass.getPassword()));
            body.put("workspaceId", SessionContext.getInstance().getWorkspaceId());

            SwingWorker<Void, Void> w = new SwingWorker<>() {
                @Override protected Void doInBackground() throws Exception {
                    ApiClient.post("/auth/agregar-agente", body);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        showSuccess("Usuario agente creado exitosamente.");
                    } catch (Exception ex) { showError(ex.getMessage()); }
                }
            };
            w.execute();
        });
        buttons.add(btnCancel);
        buttons.add(btnGuardar);

        gbc.gridy = 3; gbc.insets = new Insets(16, 0, 0, 0);
        panel.add(buttons, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

            private void showAcciones(String idAgente) {
                // Buscar datos actuales del agente en la tabla
                int row = -1;
                for (int i = 0; i < tabla.getRowCount(); i++) {
                    if (tabla.getValueAt(i, 0).toString().equals(idAgente)) {
                        row = i; break;
                    }
                }
                if (row == -1) return;

                String nombre = tabla.getValueAt(row, 1).toString();
                String email  = tabla.getValueAt(row, 3).toString();
                String nivel  = tabla.getValueAt(row, 4).toString();

                String[] opciones = {"✏ Editar", "🗑 Eliminar", "Cancelar"};
                int opcion = JOptionPane.showOptionDialog(this,
                    "<html><b>" + idAgente + "</b><br><small>" + nombre + "</small></html>",
                    "Acciones del Agente",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, opciones, opciones[2]);

                if (opcion == 0) {
                    showFormEditar(idAgente, nombre, email, nivel);
                } else if (opcion == 1) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Eliminar el agente " + idAgente + " — " + nombre + "?",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
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

            // AGREGA este método nuevo después de showAcciones():
            private void showFormEditar(String idAgente, String nombreActual,
                                         String emailActual, String nivelActual) {
                JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Editar Agente — " + idAgente, true);
                dialog.setSize(480, 360);
                dialog.setLocationRelativeTo(this);

                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(MainFrame.BG_CARD);
                panel.setBorder(new EmptyBorder(24, 28, 24, 28));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(6, 0, 6, 0);
                gbc.fill   = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1.0;

                // Encabezado del diálogo
                gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
                JPanel headerCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
                headerCard.setBackground(new Color(0x1A2744));
                headerCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MainFrame.ACCENT_BLUE, 1),
                    new EmptyBorder(4, 8, 4, 8)
                ));
                JLabel idLabel = new JLabel("ID: " + idAgente);
                idLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
                idLabel.setForeground(MainFrame.ACCENT_BLUE);
                headerCard.add(idLabel);
                panel.add(headerCard, gbc);

                gbc.gridwidth = 1;
                gbc.insets    = new Insets(8, 0, 6, 0);

                JTextField fNombre = buildFormField(); fNombre.setText(nombreActual);
                JTextField fEmail  = buildFormField(); fEmail.setText(emailActual);
                JTextField fTel    = buildFormField(); fTel.setText("");

                JComboBox<String> fNivel = new JComboBox<>(
                    new String[]{"JUNIOR", "INTERMEDIO", "SENIOR"});
                fNivel.setBackground(new Color(0x334155));
                fNivel.setForeground(MainFrame.TEXT_PRIMARY);
                fNivel.setSelectedItem(nivelActual);

                addFormRow(panel, gbc, 1, "Nombre Completo", fNombre);
                addFormRow(panel, gbc, 2, "Email",            fEmail);
                addFormRow(panel, gbc, 3, "Teléfono",         fTel);
                addFormRow(panel, gbc, 4, "Nivel",            fNivel);

                JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                buttons.setOpaque(false);
                JButton btnCancel  = buildSecondaryButton("Cancelar");
                JButton btnGuardar = buildPrimaryButton("Guardar Cambios");
                btnGuardar.setBackground(MainFrame.ACCENT_GREEN);

                btnCancel.addActionListener(e -> dialog.dispose());
                btnGuardar.addActionListener(e -> {
                    String nuevoNombre = fNombre.getText().trim();
                    String nuevoEmail  = fEmail.getText().trim();
                    String nuevoTel    = fTel.getText().trim();
                    String nuevoNivel  = fNivel.getSelectedItem().toString();

                    if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog,
                            "Nombre y Email son obligatorios.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    btnGuardar.setEnabled(false);
                    btnGuardar.setText("Guardando...");

                    // Llamada PUT /api/agentes/{id}
                    java.util.Map<String, Object> body = new HashMap<>();
                    body.put("nombreCompleto",   nuevoNombre);
                    body.put("email",            nuevoEmail);
                    body.put("telefono",         nuevoTel);
                    body.put("nivelExperiencia", nuevoNivel);

                    SwingWorker<Void, Void> w = new SwingWorker<>() {
                        @Override protected Void doInBackground() throws Exception {
                            ApiClient.put("/agentes/" + idAgente, body);
                            return null;
                        }
                        @Override protected void done() {
                            try {
                                get();
                                dialog.dispose();
                                loadAgentes();
                                showSuccess("Agente actualizado exitosamente.");
                            } catch (Exception ex) {
                                btnGuardar.setEnabled(true);
                                btnGuardar.setText("Guardar Cambios");
                                showError(ex.getMessage());
                            }
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

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private void addFormRow(JPanel p, GridBagConstraints gbc,
                             int row, String label, JComponent field) {
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
            new EmptyBorder(6, 10, 6, 10)));
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
            new EmptyBorder(4, 10, 4, 10)));
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
        JOptionPane.showMessageDialog(this, msg, "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}