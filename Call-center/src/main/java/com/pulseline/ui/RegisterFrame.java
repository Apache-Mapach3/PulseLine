package com.pulseline.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulseline.ui.utils.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla para crear un nuevo workspace (solo para Administradores).
 */
public class RegisterFrame extends JFrame {

    private JTextField txtWorkspace;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JLabel lblError;
    private JLabel lblCodigo;
    private JButton btnCrear;

    public RegisterFrame() {
        setTitle("PulseLine — Crear Workspace");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0x0F172A));

        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x10B981),
                        0, getHeight(), new Color(0x059669));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(8, 0));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0F172A));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(48, 40, 40, 40));

        JLabel title = new JLabel("Crear Workspace");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(0xF1F5F9));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Registra tu empresa y cuenta de administrador");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(0x94A3B8));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtWorkspace = buildInput();
        txtUsuario   = buildInput();
        txtPassword  = new JPasswordField(); styleInput(txtPassword);
        txtConfirm   = new JPasswordField(); styleInput(txtConfirm);

        lblError = new JLabel(" ");
        lblError.setForeground(new Color(0xEF4444));
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel de código generado (oculto hasta que se crea)
        lblCodigo = new JLabel(" ");
        lblCodigo.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblCodigo.setForeground(new Color(0x10B981));
        lblCodigo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel codigoBox = new JPanel(new BorderLayout());
        codigoBox.setBackground(new Color(0x0D2818));
        codigoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x10B981), 1),
            new EmptyBorder(12, 16, 12, 16)
        ));
        codigoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        codigoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        codigoBox.setVisible(false);

        JLabel codigoLabel = new JLabel("Código de acceso para tus agentes:");
        codigoLabel.setForeground(new Color(0x6EE7B7));
        codigoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        codigoBox.add(codigoLabel, BorderLayout.NORTH);
        codigoBox.add(lblCodigo, BorderLayout.CENTER);

        btnCrear = buildPrimaryBtn("Crear Workspace");
        btnCrear.addActionListener(e -> doCrear(codigoBox));

        JButton btnVolver = buildLinkBtn("¿Ya tienes cuenta? Iniciar sesión");
        btnVolver.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(36));
        panel.add(buildLabel("Nombre de la empresa / workspace"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtWorkspace);
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildLabel("Usuario administrador"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtUsuario);
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildLabel("Contraseña (mín. 6 caracteres)"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildLabel("Confirmar contraseña"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtConfirm);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(12));
        panel.add(codigoBox);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnCrear);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnVolver);

        root.add(left, BorderLayout.WEST);
        root.add(panel, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void doCrear(JPanel codigoBox) {
        String workspace = txtWorkspace.getText().trim();
        String usuario   = txtUsuario.getText().trim();
        String pass      = new String(txtPassword.getPassword());
        String confirm   = new String(txtConfirm.getPassword());

        if (workspace.isEmpty() || usuario.isEmpty() || pass.isEmpty()) {
            lblError.setText("Completa todos los campos.");
            return;
        }
        if (!pass.equals(confirm)) {
            lblError.setText("Las contraseñas no coinciden.");
            return;
        }
        if (pass.length() < 6) {
            lblError.setText("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        btnCrear.setEnabled(false);
        btnCrear.setText("Creando...");
        lblError.setText(" ");

        SwingWorker<JsonNode, Void> worker = new SwingWorker<>() {
            @Override protected JsonNode doInBackground() throws Exception {
                Map<String, Object> body = new HashMap<>();
                body.put("nombreWorkspace", workspace);
                body.put("username",        usuario);
                body.put("password",        pass);
                return ApiClient.post("/auth/registro", body);
            }
            @Override protected void done() {
                try {
                    JsonNode res = get();
                    String codigo = res.path("codigo").asText();
                    lblCodigo.setText(codigo);
                    codigoBox.setVisible(true);
                    btnCrear.setText("✓ Workspace Creado");
                    btnCrear.setBackground(new Color(0x10B981));

                    // Copiar al portapapeles
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new java.awt.datatransfer.StringSelection(codigo), null);

                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "¡Workspace creado!\n\nTu código de acceso es:\n" + codigo +
                        "\n\n(Copiado al portapapeles)\n\nCompártelo con tus agentes para que puedan unirse.",
                        "Workspace Creado", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    lblError.setText("Error al crear workspace: " + ex.getMessage());
                    btnCrear.setEnabled(true);
                    btnCrear.setText("Crear Workspace");
                }
            }
        };
        worker.execute();
    }

    private JTextField buildInput() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    private void styleInput(JComponent f) {
        f.setBackground(new Color(0x1E293B));
        f.setForeground(new Color(0xF1F5F9));
        if (f instanceof JTextField tf) tf.setCaretColor(new Color(0xF1F5F9));
        if (f instanceof JPasswordField pf) pf.setCaretColor(new Color(0xF1F5F9));
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x334155), 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JLabel buildLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(0x94A3B8));
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton buildPrimaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0x10B981));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(12, 0, 12, 0));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    private JButton buildLinkBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0x0F172A));
        b.setForeground(new Color(0x3B82F6));
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }
}