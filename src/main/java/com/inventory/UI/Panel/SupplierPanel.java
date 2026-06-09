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

public class SupplierPanel extends JPanel {

    private final SupplierService service;
    private final OrderService orderService;
    private final Runnable onUpdate;

    private final DefaultTableModel supplierModel;
    private final JTable supplierTable;
    private final DefaultTableModel ordersModel;
    private final JTable ordersTable;
    private final JTextField searchField;

    public SupplierPanel(SupplierService service, OrderService orderService, Runnable onUpdate) {
        this.service = service;
        this.orderService = orderService;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 28));

        // ── Supplier table ────────────────────────────────────────────
        supplierModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Phone", "Email"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        supplierTable = new JTable(supplierModel);
        styleTable(supplierTable);
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshOrders();
        });

        // ── Orders sub-table ──────────────────────────────────────────
        ordersModel = new DefaultTableModel(new String[]{"Order ID", "Total", "# Items", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        styleTable(ordersTable);
        
        // Status Renderer
        ordersTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String status = v == null ? "" : v.toString();
                setForeground("COMPLETED".equals(status) ? new Color(34, 197, 94) : 
                              "CANCELLED".equals(status) ? new Color(239, 68, 68) : new Color(250, 204, 21));
                setBackground(sel ? new Color(55, 55, 60) : new Color(30, 30, 35));
                return this;
            }
        });

        // ── Toolbar ───────────────────────────────────────────────────
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.addActionListener(e -> refresh());

        ModernButton addBtn = new ModernButton("+ Add", new Color(34, 197, 94));
        ModernButton editBtn = new ModernButton("Edit", new Color(99, 102, 241));
        ModernButton deleteBtn = new ModernButton("Delete", new Color(220, 53, 69));

        addBtn.addActionListener(e -> addSupplier());
        editBtn.addActionListener(e -> editSupplier());
        deleteBtn.addActionListener(e -> deleteSupplier());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(searchField);
        top.add(addBtn);
        top.add(editBtn);
        top.add(deleteBtn);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(supplierTable), new JScrollPane(ordersTable));
        split.setResizeWeight(0.55);
        split.setOpaque(false);
        split.setBorder(null);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    // --- CRUD Methods ---
    private void addSupplier() {
        JTextField name = new JTextField(), contact = new JTextField(), phone = new JTextField(), email = new JTextField();
        if (JOptionPane.showConfirmDialog(this, buildForm(name, contact, phone, email), "Add Supplier", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            service.add(new Supplier(0, name.getText(), contact.getText(), phone.getText(), email.getText(), "Active"));
            refresh();
            notifyUpdate();
        }
    }

    private void editSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) return;
        Supplier s = service.getById(Integer.parseInt(supplierModel.getValueAt(row, 0).toString()));
        JTextField name = new JTextField(s.getName()), contact = new JTextField(s.getContact()), phone = new JTextField(s.getPhone()), email = new JTextField(s.getEmail());
        if (JOptionPane.showConfirmDialog(this, buildForm(name, contact, phone, email), "Edit", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            s.setName(name.getText()); s.setContact(contact.getText()); s.setPhone(phone.getText()); s.setEmail(email.getText());
            service.update(s);
            refresh();
            notifyUpdate();
        }
    }

    private void deleteSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row != -1 && JOptionPane.showConfirmDialog(this, "Delete supplier?") == JOptionPane.YES_OPTION) {
            service.delete(Integer.parseInt(supplierModel.getValueAt(row, 0).toString()));
            refresh();
            notifyUpdate();
        }
    }

    public void refresh() {
    // 1. Check if the table or model is initialized
    if (supplierTable == null || supplierModel == null) return;

    // 2. Save selection state
    int selRow = supplierTable.getSelectedRow();
    
    // 3. Perform data loading inside a try-catch to prevent a full crash
    try {
        supplierModel.setRowCount(0);
        for (Supplier s : service.getAll()) {
            supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContact(), s.getPhone(), s.getEmail()});
        }
    } catch (Exception e) {
        System.err.println("Error during refresh: " + e.getMessage());
    }
}

    private void refreshOrders() {
        ordersModel.setRowCount(0);
        int row = supplierTable.getSelectedRow();
        if (row == -1) return;
        for (Order o : orderService.getBySupplierId(Integer.parseInt(supplierModel.getValueAt(row, 0).toString()))) {
            ordersModel.addRow(new Object[]{"#ORD-" + o.getId(), String.format("$%.2f", o.getTotal()), o.getItems().size(), o.getStatus().name()});
        }
    }

    private JPanel buildForm(JTextField n, JTextField c, JTextField p, JTextField e) {
        JPanel f = new JPanel(new GridLayout(0, 1, 4, 4));
        f.add(new JLabel("Name")); f.add(n); f.add(new JLabel("Contact")); f.add(c);
        f.add(new JLabel("Phone")); f.add(p); f.add(new JLabel("Email")); f.add(e);
        return f;
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(30, 30, 35));
        t.setForeground(Color.LIGHT_GRAY);
        t.setRowHeight(26);
        t.getTableHeader().setBackground(new Color(40, 40, 50));
        t.getTableHeader().setForeground(Color.WHITE);
    }

    private void notifyUpdate() { if (onUpdate != null) onUpdate.run(); }
}