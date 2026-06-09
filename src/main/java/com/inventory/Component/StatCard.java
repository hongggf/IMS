package com.inventory.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatCard extends JPanel {

    private final JLabel valueLabel;
    private final int cornerRadius = 15;

    public StatCard(String title, String value) {
        setLayout(new BorderLayout());
        setOpaque(false); // Necessary for custom painting
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Container panel
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(180, 180, 185));

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBorder(new EmptyBorder(10, 0, 5, 0));

        JLabel footer = new JLabel("Updated just now");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(new Color(100, 100, 110));

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);

        add(content);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw Rounded Background
        g2.setColor(new Color(30, 30, 36));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        super.paintComponent(g);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}