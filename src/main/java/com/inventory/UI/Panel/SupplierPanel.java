package com.inventory.UI.Panel;

import com.inventory.model.ModernButton;
import com.inventory.model.Order;
import com.inventory.model.Supplier;
import com.inventory.service.OrderService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SupplierPanel extends JPanel {

    private final SupplierService service;
    private final OrderService orderService;
    private final Runnable onUpdate;
    private final ActivityLogPanel logPanel;

    private final DefaultTableModel supplierModel;
    private final JTable supplierTable;
    private final DefaultTableModel ordersModel;
    private final JTable ordersTable;
    private final JTextField searchField;

    public SupplierPanel(SupplierService service, OrderService orderService, 
                        Runnable onUpdate, ActivityLogPanel logPanel) {
        this.service = service;
        this.orderService = orderService;
        this.onUpdate = onUpdate;
        this.logPanel = logPanel;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 28));

        // Supplier Table
        supplierModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Phone", "Email", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        supplierTable = new JTable(supplierModel);
        styleTable(supplierTable);

        supplierTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) refreshOrders();
            }
        });

        // Orders Table
        ordersModel = new DefaultTableModel(new String[]{"Order ID", "Total", "Items", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        styleTable(ordersTable);
        ordersTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        // Toolbar
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(220, 32));
        styleInput(searchField);
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { refresh(); }
        });

        ModernButton addBtn = new ModernButton("+ Add Supplier", new Color(34, 197, 94));
        ModernButton editBtn = new ModernButton("Edit", new Color(99, 102, 241));
        ModernButton deleteBtn = new ModernButton("Delete", new Color(239, 68, 68));

        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { addSupplier(); }
        });
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { editSupplier(); }
        });
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { deleteSupplier(); }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(searchField);
        top.add(addBtn);
        top.add(editBtn);
        top.add(deleteBtn);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(supplierTable), new JScrollPane(ordersTable));
        split.setResizeWeight(0.6);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    private void addSupplier() {
        JTextField name = new JTextField(), contact = new JTextField(), 
                   phone = new JTextField(), email = new JTextField();
        JPanel form = buildForm(name, contact, phone, email);

        if (JOptionPane.showConfirmDialog(this, form, "Add Supplier", 
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String n = name.getText().trim();
                if (n.isEmpty()) throw new Exception("Name is required");

                Supplier s = new Supplier(0, n, contact.getText().trim(),
                        phone.getText().trim(), email.getText().trim(), "Active");

                service.add(s);
                refresh();
                if (onUpdate != null) onUpdate.run();
                if (logPanel != null) logPanel.logActivity("Supplier Added", n);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier.");
            return;
        }

        Supplier s = service.getById(Integer.parseInt(supplierModel.getValueAt(row, 0).toString()));
        if (s == null) return;

        JTextField name = new JTextField(s.getName());
        JTextField contact = new JTextField(s.getContact());
        JTextField phone = new JTextField(s.getPhone());
        JTextField email = new JTextField(s.getEmail());

        JPanel form = buildForm(name, contact, phone, email);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Supplier", 
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                s.setName(name.getText().trim());
                s.setContact(contact.getText().trim());
                s.setPhone(phone.getText().trim());
                s.setEmail(email.getText().trim());

                service.update(s);
                refresh();
                if (onUpdate != null) onUpdate.run();
                if (logPanel != null) logPanel.logActivity("Supplier Updated", name.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        }
    }

    private void deleteSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) return;

        String name = supplierModel.getValueAt(row, 1).toString();

        if (JOptionPane.showConfirmDialog(this, "Delete supplier: " + name + "?", 
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            service.delete(Integer.parseInt(supplierModel.getValueAt(row, 0).toString()));
            refresh();
            if (onUpdate != null) onUpdate.run();
            if (logPanel != null) logPanel.logActivity("Supplier Deleted", name);
        }
    }

    public void refresh() {
        supplierModel.setRowCount(0);
        String q = searchField.getText().toLowerCase().trim();

        for (Supplier s : service.getAll()) {
            if (q.isEmpty() || s.getName().toLowerCase().contains(q)) {
                supplierModel.addRow(new Object[]{
                        s.getId(), s.getName(), s.getContact(), 
                        s.getPhone(), s.getEmail(), "Active"
                });
            }
        }
        refreshOrders();
    }

    private void refreshOrders() {
        ordersModel.setRowCount(0);
        int row = supplierTable.getSelectedRow();
        if (row == -1) return;

        int supplierId = Integer.parseInt(supplierModel.getValueAt(row, 0).toString());
        for (Order o : orderService.getBySupplierId(supplierId)) {
            ordersModel.addRow(new Object[]{
                    "#ORD-" + o.getId(),
                    String.format("$%.2f", o.getTotalAmount()),
                    o.getItems().size(),
                    o.getStatus().name()
            });
        }
    }

    private JPanel buildForm(JTextField n, JTextField c, JTextField p, JTextField e) {
        JPanel f = new JPanel(new GridLayout(0, 2, 8, 8));
        f.add(new JLabel("Name")); f.add(n);
        f.add(new JLabel("Contact")); f.add(c);
        f.add(new JLabel("Phone")); f.add(p);
        f.add(new JLabel("Email")); f.add(e);
        return f;
    }

    private void styleInput(JTextField f) {
        f.setBackground(new Color(35, 35, 40));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(30, 30, 35));
        t.setForeground(Color.LIGHT_GRAY);
        t.setRowHeight(26);
        t.setSelectionBackground(new Color(55, 55, 70));
        t.getTableHeader().setBackground(new Color(40, 40, 50));
        t.getTableHeader().setForeground(Color.WHITE);
    }

    private void notifyUpdate() {
        if (onUpdate != null) onUpdate.run();
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value == null ? "" : value.toString();
            if ("COMPLETED".equals(status)) setForeground(new Color(34, 197, 94));
            else if ("CANCELLED".equals(status)) setForeground(new Color(239, 68, 68));
            else setForeground(new Color(250, 204, 21));
            return this;
        }
    }
}