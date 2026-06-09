package com.inventory.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    // ── Modern dark UI palette (UI ONLY) ─────────────────────
    private static final Color BG = new Color(18, 18, 22);
    private static final Color CARD = new Color(28, 28, 34);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color TEXT = new Color(235, 235, 235);
    private static final Color MUTED = new Color(160, 160, 160);

    public LoginFrame() {

        setTitle("Inventory System — Login");
        setSize(420, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);

        // ── Outer container ─────────────────────────────
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        // ── Card ─────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setPreferredSize(new Dimension(340, 390));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 55)),
                new EmptyBorder(35, 35, 35, 35)
        ));

        // ── Title ─────────────────────────────
        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // ── Inputs ─────────────────────────────
        JLabel userLbl = label("Username");
        JTextField txtUser = input();

        JLabel passLbl = label("Password");
        JPasswordField txtPass = new JPasswordField();
        styleInput(txtPass);

        // ── Show password ─────────────────────────────
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setBackground(CARD);
        showPass.setForeground(MUTED);
        showPass.setFocusPainted(false);
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPass.setAlignmentX(LEFT_ALIGNMENT);

        showPass.addActionListener(e ->
                txtPass.setEchoChar(showPass.isSelected() ? (char) 0 : '•')
        );

        // ── Error label ─────────────────────────────
        JLabel err = new JLabel(" ");
        err.setForeground(new Color(239, 68, 68));
        err.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        err.setAlignmentX(LEFT_ALIGNMENT);

        // ── Button ─────────────────────────────
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setBackground(ACCENT);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // hover effect (pure UI enhancement)
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(37, 99, 235));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(ACCENT);
            }
        });

        // ── LOGIN LOGIC (UNCHANGED — DO NOT TOUCH) ─────────────────────
        loginBtn.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                err.setText("Please fill in all fields.");
                return;
            }

            if (user.equals("admin") && pass.equals("1234")) {

                try {
                    new com.inventory.UI.DashboardFrame();
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    err.setText("System error.");
                }

            } else {
                err.setText("Invalid credentials.");
            }
        });

        // ── Layout ─────────────────────────────
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(25));

        card.add(userLbl);
        card.add(txtUser);
        card.add(Box.createVerticalStrut(15));

        card.add(passLbl);
        card.add(txtPass);
        card.add(Box.createVerticalStrut(10));

        card.add(showPass);
        card.add(Box.createVerticalStrut(10));

        card.add(err);
        card.add(Box.createVerticalStrut(18));

        card.add(loginBtn);

        outer.add(card);
        add(outer);

        setVisible(true);
    }

    // ── helpers (unchanged structure, UI improved) ─────────────────────
    private JTextField input() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    private void styleInput(JTextField f) {
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setBackground(new Color(20, 20, 25));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);

        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 55)),
                new EmptyBorder(6, 10, 6, 10)
        ));

        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(180, 180, 180));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}