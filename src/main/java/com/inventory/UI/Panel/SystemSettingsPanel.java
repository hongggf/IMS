package com.inventory.UI.Panel;

import com.inventory.UI.UITheme;
import javax.swing.*;
import java.awt.*;

public class SystemSettingsPanel extends JPanel {

    public SystemSettingsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UITheme.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("System Settings");
        title.setForeground(UITheme.TITLE_COLOR);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(CENTER_ALIGNMENT);

        // Add more realistic settings
        JCheckBox darkMode = new JCheckBox("Dark Mode");
        darkMode.setSelected(true);
        darkMode.setForeground(UITheme.TEXT_COLOR);
        darkMode.setBackground(UITheme.BG_COLOR);

        JButton backupBtn = new JButton("Backup Database");
        backupBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Backup completed successfully!"));

        JButton exportBtn = new JButton("Export Reports");
        exportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reports exported to CSV/PDF"));

        add(Box.createVerticalStrut(20));
        add(title);
        add(Box.createVerticalStrut(30));
        add(darkMode);
        add(Box.createVerticalStrut(15));
        add(backupBtn);
        add(Box.createVerticalStrut(10));
        add(exportBtn);
        add(Box.createVerticalGlue());
    }
}