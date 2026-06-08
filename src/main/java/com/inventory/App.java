package com.inventory;

import com.formdev.flatlaf.FlatDarkLaf;
import com.inventory.UI.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            UIManager.put("TextComponent.arc", 5);
            UIManager.put("Button.arc", 5);
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
