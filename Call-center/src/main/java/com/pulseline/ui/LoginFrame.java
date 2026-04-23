package com.pulseline.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.pulseline.ui.utils.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla de inicio de sesión de PulseLine.
 */
public class LoginFrame extends JFrame {

    private JTextField txtCodigo;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private JButton btnLogin;
    private JButton btnRegistrar;

    public LoginFrame() {
        setTitle("PulseLine — Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(new Color(0x0F172A));
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0x0F172A));

        root.add(buildLeft(),  BorderLayout.WEST);
        root.add(buildForm(),  BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildLeft() {
        // Franja de color izquierda
        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x3B82F6),
                        0, getHeight(), new Color(0x1D4ED8));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(8, 0));
        return left;
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x0F172A));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(48, 40, 40, 40));

        // Logo
        JLabel logo = new JLabel("P") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x3B82F6));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("P", (getWidth() - fm.stringWidth("P")) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(52, 52));
        logo.setMaximumSize(new Dimension(52, 52));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("PulseLine");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(0xF1F5F9));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Inicia sesión en tu workspace");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(0x94A3B8));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Campos
        txtCodigo   = buildInput();
        txtUsuario  = buildInput();
        txtPassword = new JPasswordField();
        styleInput(txtPassword);

        lblError = new JLabel(" ");
        lblError.setForeground(new Color(0xEF4444));
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = buildPrimaryBtn("Iniciar Sesión");
        btnLogin.addActionListener(e -> doLogin());

        btnRegistrar = buildLinkBtn("¿No tienes workspace? Crear uno");
        btnRegistrar.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });

        // Layout
        panel.add(logo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(36));
        panel.add(buildLabel("Código de Workspace"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtCodigo);
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildLabel("Usuario"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtUsuario);
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildLabel("Contraseña"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnRegistrar);

        return panel;
    }

    private void doLogin() {
        String codigo   = txtCodigo.getText().trim();
        String usuario  = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (codigo.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            lblError.setText("Completa todos los campos.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Verificando...");
        lblError.setText(" ");

        SwingWorker<JsonNode, Void> worker = new SwingWorker<>() {
            @Override protected JsonNode doInBackground() throws Exception {
                Map<String, Object> body = new HashMap<>();
                body.put("username",         usuario);
                body.put("password",         password);
                body.put("codigoWorkspace",  codigo);
                return ApiClient.post("/auth/login", body);
            }
            @Override protected void done() {
                try {
                    JsonNode res = get();
                    SessionContext.getInstance().iniciarSesion(
                        res.path("usuarioId").asText(),
                        res.path("username").asText(),
                        res.path("rol").asText(),
                        res.path("workspaceId").asText(),
                        res.path("workspaceNombre").asText()
                    );
                    dispose();
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception ex) {
                    lblError.setText("Credenciales incorrectas o workspace inválido.");
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");
                }
            }
        };
        worker.execute();
    }

    // ── HELPERS DE UI ─────────────────────────────────────────────────────────

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
        b.setBackground(new Color(0x3B82F6));
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