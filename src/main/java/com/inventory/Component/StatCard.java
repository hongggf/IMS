package com.inventory.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatCard extends JPanel {

    private final JLabel valueLabel;

    public StatCard(String title, String value) {

        setLayout(new BorderLayout());
        setBackground(new Color(28, 28, 32));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55,55,60), 1),
                new EmptyBorder(18,18,18,18)
        ));

        // Top section
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(170,170,175));

        JLabel icon = new JLabel("●");
        icon.setForeground(new Color(80,120,255));
        icon.setFont(new Font("Segoe UI", Font.BOLD, 14));

        top.add(titleLabel, BorderLayout.WEST);
        top.add(icon, BorderLayout.EAST);

        // Value section
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        valueLabel.setForeground(Color.WHITE);

        // Footer
        JLabel footer = new JLabel("Updated in real-time");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(120,120,125));

        add(top, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}