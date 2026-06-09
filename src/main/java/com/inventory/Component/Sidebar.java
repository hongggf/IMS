package com.inventory.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sidebar extends JPanel {

    public interface NavListener { void onNavigate(String screen); }
    private NavListener listener;
    public void setNavListener(NavListener listener) { this.listener = listener; }

    private static final Color BG = new Color(20, 20, 25);
    private static final Color ACTIVE_BG = new Color(35, 35, 45);
    private static final Color TEXT = new Color(170, 170, 180);
    private static final Color ACCENT = new Color(75, 140, 250);

    public final JButton dashboardBtn, productBtn, supplierBtn, orderBtn, reportBtn;

    public Sidebar() {
        setPreferredSize(new Dimension(240, 0));
        setBackground(BG);
        setLayout(new BorderLayout());

        // 1. Top Section: Header
        JLabel header = new JLabel("IMS SYSTEM");
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(new EmptyBorder(30, 25, 30, 0));
        add(header, BorderLayout.NORTH);

        // 2. Middle Section: Navigation
        JPanel navGroup = new JPanel();
        navGroup.setLayout(new BoxLayout(navGroup, BoxLayout.Y_AXIS));
        navGroup.setOpaque(false);

        dashboardBtn = createBtn("Dashboard", "dashboard");
        productBtn = createBtn("Products", "products");
        supplierBtn = createBtn("Suppliers", "suppliers");
        orderBtn = createBtn("Orders", "orders");
        reportBtn = createBtn("Reports", "reports");

        JButton[] btns = {dashboardBtn, productBtn, supplierBtn, orderBtn, reportBtn};
        for (JButton b : btns) {
            navGroup.add(b);
            navGroup.add(Box.createVerticalStrut(5));
        }
        add(navGroup, BorderLayout.CENTER);

        // 3. Bottom Section: Profile Settings
        add(createProfileSection(), BorderLayout.SOUTH);

        setActive(dashboardBtn);
    }

    private JPanel createProfileSection() {
        JPanel profile = new JPanel(new BorderLayout(10, 0));
        profile.setBackground(new Color(15, 15, 20));
        profile.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel avatar = new JLabel("AD", SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(true);
        avatar.setBackground(ACCENT);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel name = new JLabel("<html>Admin User<br/><font color='#777777'>Settings</font></html>");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        profile.add(avatar, BorderLayout.WEST);
        profile.add(name, BorderLayout.CENTER);
        return profile;
    }

    private JButton createBtn(String text, String cmd) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setForeground(TEXT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(BG);
        btn.setBorder(new EmptyBorder(0, 25, 0, 0));

        btn.addActionListener(e -> {
            if (listener != null) listener.onNavigate(cmd);
            setActive(btn);
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("active"))) btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("active"))) btn.setForeground(TEXT);
            }
        });
        return btn;
    }

    public void setActive(JButton active) {
        JButton[] all = {dashboardBtn, productBtn, supplierBtn, orderBtn, reportBtn};
        for (JButton b : all) {
            boolean isActive = (b == active);
            b.putClientProperty("active", isActive);
            b.setBackground(isActive ? ACTIVE_BG : BG);
            b.setForeground(isActive ? Color.WHITE : TEXT);
            b.setBorder(isActive ? BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, ACCENT),
                new EmptyBorder(0, 20, 0, 0)
            ) : new EmptyBorder(0, 25, 0, 0));
        }
    }
}