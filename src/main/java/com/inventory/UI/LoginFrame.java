package com.inventory.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    // Clean, modern color palette
    private static final Color BG = new Color(240, 242, 245);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(55, 138, 221);
    private static final Color TEXT = new Color(33, 33, 33);
    private static final Color MUTED = new Color(117, 117, 117);

    public LoginFrame() {
        setTitle("Inventory System — Login");
        setSize(420, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);

        // Main container with padding
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(360, 380));

        // Title Section
        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Inputs
        JLabel userLbl = label("Username");
        JTextField txtUser = input();

        JLabel passLbl = label("Password");
        JPasswordField txtPass = new JPasswordField();
        styleInput(txtPass);

        // Show Password
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setBackground(CARD);
        showPass.setForeground(MUTED);
        showPass.setFocusPainted(false);
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPass.setAlignmentX(LEFT_ALIGNMENT);
        showPass.addActionListener(e -> txtPass.setEchoChar(showPass.isSelected() ? (char) 0 : '•'));

        // Error
        JLabel err = new JLabel(" ");
        err.setForeground(new Color(220, 53, 69));
        err.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        err.setAlignmentX(LEFT_ALIGNMENT);

        // Button
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginBtn.setBackground(ACCENT);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Logic (Unchanged)
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

        // Layout Assembly
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(userLbl);
        card.add(txtUser);
        card.add(Box.createVerticalStrut(15));
        card.add(passLbl);
        card.add(txtPass);
        card.add(Box.createVerticalStrut(10));
        card.add(showPass);
        card.add(Box.createVerticalStrut(10));
        card.add(err);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);

        outer.add(card);
        add(outer);
        setVisible(true);
    }

    private JTextField input() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    private void styleInput(JTextField f) {
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(80, 80, 80));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}