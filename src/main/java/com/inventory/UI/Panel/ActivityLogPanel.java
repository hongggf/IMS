package com.inventory.UI.Panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogPanel extends JPanel {

    private final DefaultTableModel model;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ActivityLogPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 22));

        model = new DefaultTableModel(new String[]{"Time", "Action", "Details"}, 0);
        JTable table = new JTable(model);
        styleTable(table);

        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    public void logActivity(String action, String details) {
        String time = LocalDateTime.now().format(FORMATTER);
        model.insertRow(0, new Object[]{time, action, details}); // Newest on top
    }

    public void refresh() {
        // Keep existing logs + demo data
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(28, 28, 34));
        t.setForeground(Color.LIGHT_GRAY);
        t.setRowHeight(28);
        t.setGridColor(new Color(45, 45, 55));
        t.getTableHeader().setBackground(new Color(35, 35, 45));
        t.getTableHeader().setForeground(Color.WHITE);
    }
}