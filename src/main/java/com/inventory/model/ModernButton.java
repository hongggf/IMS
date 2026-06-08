package com.inventory.model;

import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {

    public ModernButton(String text, Color bg) {

        super(text);

        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setBackground(bg);
        setForeground(Color.WHITE);

        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setPreferredSize(new Dimension(140, 36));

        setOpaque(true);
    }
}