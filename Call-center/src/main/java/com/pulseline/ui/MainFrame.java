package com.pulseline.ui;

import com.pulseline.ui.components.SidebarButton;
import com.pulseline.ui.panels.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    public static final Color BG_DARK       = new Color(0x0F172A);
    public static final Color BG_SIDEBAR    = new Color(0x1E293B);
    public static final Color BG_CARD       = new Color(0x1E293B);
    public static final Color BG_PANEL      = new Color(0x0F172A);
    public static final Color ACCENT_BLUE   = new Color(0x3B82F6);
    public static final Color ACCENT_GREEN  = new Color(0x10B981);
    public static final Color ACCENT_ORANGE = new Color(0xF59E0B);
    public static final Color ACCENT_RED    = new Color(0xEF4444);
    public static final Color TEXT_PRIMARY  = new Color(0xF1F5F9);
    public static final Color TEXT_MUTED    = new Color(0x94A3B8);
    public static final Color BORDER_COLOR  = new Color(0x334155);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private SidebarButton activeButton;

    public MainFrame() {
        SessionContext session = SessionContext.getInstance();
        setTitle("PulseLine — " + session.getWorkspaceNombre() +
                 "  [" + session.getRol() + ": " + session.getNombreUsuario() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 1));

        sidebar.add(buildBrand());
        sidebar.add(buildDivider());

        String[][] navItems = {
            {"dashboard", "⬛", "Dashboard"},
            {"agentes",   "👤", "Agentes"},
            {"llamadas",  "📞", "Llamadas"},
            {"campanias", "📋", "Campañas"},
        };

        for (String[] item : navItems) {
            SidebarButton btn = new SidebarButton(item[1], item[2]);
            final String panelId = item[0];
            btn.addActionListener(e -> showPanel(panelId, btn));
            sidebar.add(btn);
            if (panelId.equals("dashboard")) {
                btn.setActive(true);
                activeButton = btn;
            }
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildDivider());
        sidebar.add(buildUserInfo());
        sidebar.add(buildDivider());
        sidebar.add(buildStatusBar());
        return sidebar;
    }

    private JPanel buildBrand() {
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        brand.setBackground(BG_SIDEBAR);
        brand.setMaximumSize(new Dimension(230, 72));

        JLabel icon = new JLabel("P") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("P")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("P", x, y);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(36, 36));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(BG_SIDEBAR);

        JLabel title = new JLabel("PulseLine");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT_PRIMARY);

        // Nombre del workspace en lugar de "Call Center"
        JLabel subtitle = new JLabel(SessionContext.getInstance().getWorkspaceNombre());
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(TEXT_MUTED);

        text.add(title);
        text.add(subtitle);
        brand.add(icon);
        brand.add(text);
        return brand;
    }

    private JPanel buildUserInfo() {
        SessionContext session = SessionContext.getInstance();

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_SIDEBAR);
        bar.setBorder(new EmptyBorder(10, 16, 10, 16));
        bar.setMaximumSize(new Dimension(230, 72));

        String rolText  = session.isAdmin() ? "ADMIN" : "AGENTE";
        Color  rolColor = session.isAdmin() ? ACCENT_GREEN : ACCENT_ORANGE;

        JLabel lblNombre = new JLabel(session.getNombreUsuario());
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNombre.setForeground(TEXT_PRIMARY);

        JLabel lblRol = new JLabel("  " + rolText + "  ");
        lblRol.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblRol.setForeground(rolColor);
        lblRol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(rolColor, 1),
            new EmptyBorder(1, 4, 1, 4)
        ));
        lblRol.setOpaque(true);
        lblRol.setBackground(new Color(
            rolColor.getRed(), rolColor.getGreen(), rolColor.getBlue(), 30));

        JButton btnLogout = new JButton("Salir");
        btnLogout.setBackground(new Color(0x334155));
        btnLogout.setForeground(TEXT_MUTED);
        btnLogout.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnLogout.setBorder(new EmptyBorder(4, 10, 4, 10));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Cerrar sesión?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SessionContext.getInstance().cerrarSesion();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(lblNombre);
        left.add(Box.createVerticalStrut(4));

        JPanel rolRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolRow.setOpaque(false);
        rolRow.add(lblRol);
        left.add(rolRow);

        bar.add(left, BorderLayout.WEST);
        bar.add(btnLogout, BorderLayout.EAST);
        return bar;
    }

    private JSeparator buildDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bar.setBackground(BG_SIDEBAR);
        bar.setMaximumSize(new Dimension(230, 40));

        JLabel dot = new JLabel("●");
        dot.setForeground(ACCENT_GREEN);
        dot.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JLabel status = new JLabel("API activa en :8081");
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));
        status.setForeground(TEXT_MUTED);

        bar.add(dot);
        bar.add(status);
        return bar;
    }

    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_PANEL);

        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new AgentesPanel(),   "agentes");
        contentPanel.add(new LlamadasPanel(),  "llamadas");
        contentPanel.add(new CampaniasPanel(), "campanias");
        return contentPanel;
    }

    private void showPanel(String id, SidebarButton btn) {
        if (activeButton != null) activeButton.setActive(false);
        btn.setActive(true);
        activeButton = btn;
        cardLayout.show(contentPanel, id);
    }
}