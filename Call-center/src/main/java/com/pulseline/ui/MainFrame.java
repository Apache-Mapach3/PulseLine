package com.pulseline.ui;

import com.pulseline.ui.panels.*;
import com.pulseline.ui.components.SidebarButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ventana principal de PulseLine.
 * Estructura: Sidebar izquierdo + Panel de contenido dinámico a la derecha.
 */
public class MainFrame extends JFrame {

    // Colores del tema
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
        setTitle("PulseLine — Call Center Management");
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

    // ─── SIDEBAR ──────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 1));

        // Logo / Brand
        sidebar.add(buildBrand());
        sidebar.add(buildDivider());

        // Navigation items
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

            // Activar Dashboard por defecto
            if (panelId.equals("dashboard")) {
                btn.setActive(true);
                activeButton = btn;
            }
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildDivider());
        sidebar.add(buildStatusBar());

        return sidebar;
    }

    private JPanel buildBrand() {
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        brand.setBackground(BG_SIDEBAR);
        brand.setMaximumSize(new Dimension(230, 72));

        // Ícono circular con "P"
        JLabel icon = new JLabel("P") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

        JLabel subtitle = new JLabel("Call Center");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(TEXT_MUTED);

        text.add(title);
        text.add(subtitle);

        brand.add(icon);
        brand.add(text);
        return brand;
    }

    private JSeparator buildDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        bar.setBackground(BG_SIDEBAR);
        bar.setMaximumSize(new Dimension(230, 48));

        // Indicador de estado del servidor
        JLabel dot = new JLabel("●");
        dot.setForeground(ACCENT_GREEN);
        dot.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JLabel status = new JLabel("API activa en :8080");
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));
        status.setForeground(TEXT_MUTED);

        bar.add(dot);
        bar.add(status);
        return bar;
    }

    // ─── CONTENT AREA ─────────────────────────────────────────────────────────

    private JPanel buildContent() {
        cardLayout = new CardLayout();
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
