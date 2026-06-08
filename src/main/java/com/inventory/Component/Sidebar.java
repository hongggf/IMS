package com.inventory.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sidebar extends JPanel {

    public interface NavListener {
        void onNavigate(String screen);
    }

    private NavListener listener;

    public void setNavListener(NavListener listener) {
        this.listener = listener;
    }

    private static final Color BG = new Color(18, 18, 22);
    private static final Color ACTIVE = new Color(45, 45, 55);
    private static final Color HOVER = new Color(30, 30, 38);
    private static final Color TEXT = new Color(160, 160, 170);

    public final JButton dashboardBtn;
    public final JButton productBtn;
    public final JButton supplierBtn;
    public final JButton orderBtn;
    public final JButton reportBtn;

    public Sidebar() {

        setPreferredSize(new Dimension(220, 0));
        setBackground(BG);
        setLayout(new GridLayout(10, 1, 0, 5));

        add(createTitle());

        dashboardBtn = createBtn("Dashboard");
        productBtn = createBtn("Products");
        supplierBtn = createBtn("Suppliers");
        orderBtn = createBtn("Orders");
        reportBtn = createBtn("Reports");

        add(dashboardBtn);
        add(productBtn);
        add(supplierBtn);
        add(orderBtn);
        add(reportBtn);

        setActive(dashboardBtn);
    }

    private JLabel createTitle() {
        JLabel lbl = new JLabel("  INVENTORY");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return lbl;
    }

    private JButton createBtn(String text) {

        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(BG);
        btn.setForeground(TEXT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btn.addActionListener(e -> {
            if (listener == null) return;

            if (btn == dashboardBtn) listener.onNavigate("dashboard");
            else if (btn == productBtn) listener.onNavigate("products");
            else if (btn == supplierBtn) listener.onNavigate("suppliers");
            else if (btn == orderBtn) listener.onNavigate("orders");
            else if (btn == reportBtn) listener.onNavigate("reports");

            setActive(btn);
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("active"))) {
                    btn.setBackground(HOVER);
                    btn.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("active"))) {
                    btn.setBackground(BG);
                    btn.setForeground(TEXT);
                }
            }
        });

        return btn;
    }

    public void setActive(JButton active) {

        JButton[] all = {dashboardBtn, productBtn, supplierBtn, orderBtn, reportBtn};

        for (JButton b : all) {

            boolean isActive = (b == active);
            b.putClientProperty("active", isActive);

            if (isActive) {
                b.setBackground(ACTIVE);
                b.setForeground(Color.WHITE);
                b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                b.setBackground(BG);
                b.setForeground(TEXT);
                b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }
    }
}