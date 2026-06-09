package com.inventory.UI.Panel;

import com.inventory.UI.UITheme;
import com.inventory.model.shipment;
import com.inventory.model.ShipmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ShipmentPanel extends JPanel {

    private final ShipmentService service;
    private final ActivityLogPanel logPanel;

    private final DefaultTableModel model;
    private final JTable table;

    // This constructor matches what DashboardFrame is calling
    public ShipmentPanel(ShipmentService service, ActivityLogPanel logPanel) {
        this.service = service;
        this.logPanel = logPanel;

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_COLOR);

        model = new DefaultTableModel(
            new String[]{"Shipment ID", "Destination", "Status", "ETA"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        applyTableTheme(table);

        // Toolbar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);

        JButton addBtn = new JButton("+ New Shipment");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> addShipment());
        editBtn.addActionListener(e -> editShipment());
        deleteBtn.addActionListener(e -> deleteShipment());
        refreshBtn.addActionListener(e -> refreshShipments());

        top.add(addBtn);
        top.add(editBtn);
        top.add(deleteBtn);
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshShipments();
    }

    private void addShipment() {
        JTextField idField = new JTextField();
        JTextField destField = new JTextField();
        JTextField statusField = new JTextField("In Transit");
        JTextField etaField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.add(new JLabel("Shipment ID")); form.add(idField);
        form.add(new JLabel("Destination")); form.add(destField);
        form.add(new JLabel("Status")); form.add(statusField);
        form.add(new JLabel("ETA")); form.add(etaField);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Shipment", 
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try {
                String id = idField.getText().trim().isEmpty() ? 
                            "SHP-" + System.currentTimeMillis() : idField.getText().trim();

                shipment s = new shipment(id, destField.getText().trim(), 
                                        statusField.getText().trim(), etaField.getText().trim());

                service.add(s);
                refreshShipments();

                if (logPanel != null) {
                    logPanel.logActivity("Shipment Created", destField.getText().trim());
                }

                JOptionPane.showMessageDialog(this, "Shipment added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editShipment() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shipment first.");
            return;
        }
        JOptionPane.showMessageDialog(this, "Edit feature is ready for extension.");
    }

    private void deleteShipment() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String id = model.getValueAt(row, 0).toString();

        if (JOptionPane.showConfirmDialog(this, "Delete Shipment " + id + "?", 
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            service.delete(id);
            refreshShipments();

            if (logPanel != null) {
                logPanel.logActivity("Shipment Deleted", id);
            }
        }
    }

    public void refreshShipments() {
        model.setRowCount(0);
        for (shipment s : service.getAll()) {
            model.addRow(new Object[]{
                    s.getId(),
                    s.getDestination(),
                    s.getStatus(),
                    s.getEta()
            });
        }
    }

    private void applyTableTheme(JTable t) {
        t.setBackground(new Color(30, 30, 35));
        t.setForeground(Color.LIGHT_GRAY);
        t.setRowHeight(28);
        t.setSelectionBackground(new Color(55, 55, 70));
        t.getTableHeader().setBackground(new Color(40, 40, 50));
        t.getTableHeader().setForeground(Color.WHITE);
    }
}