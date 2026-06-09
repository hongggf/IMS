package com.inventory.UI.Panel;

import com.inventory.model.Order;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.model.ModernButton;
import com.inventory.service.OrderService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {

    private final OrderService orderService;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final Runnable onUpdate;
    private final ActivityLogPanel logPanel;

    private final DefaultTableModel orderModel;
    private final JTable orderTable;
    private final DefaultTableModel itemModel;
    private final JTable itemTable;
    private JTextField searchField;

    public OrderPanel(OrderService orderService, SupplierService supplierService,
                      ProductService productService, Runnable onUpdate, ActivityLogPanel logPanel) {

        this.orderService = orderService;
        this.supplierService = supplierService;
        this.productService = productService;
        this.onUpdate = onUpdate;
        this.logPanel = logPanel;

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(25, 25, 28));

        // Toolbar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 32));
        styleInput(searchField);
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { refresh(); }
        });

        ModernButton addOrderBtn = new ModernButton("+ New Order", new Color(34, 197, 94));
        ModernButton addItemBtn = new ModernButton("+ Add Item", new Color(59, 130, 246));
        ModernButton statusBtn = new ModernButton("Update Status", new Color(99, 102, 241));

        addOrderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { addOrder(); }
        });
        addItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { addItemToOrder(); }
        });
        statusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { changeStatus(); }
        });

        topBar.add(searchField);
        topBar.add(addOrderBtn);
        topBar.add(addItemBtn);
        topBar.add(statusBtn);

        // Order Table
        orderModel = new DefaultTableModel(new String[]{"Order ID", "Supplier", "Total", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        styleTable(orderTable);

        orderTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        orderTable.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) refreshItems();
            }
        });

        // Item Table
        itemModel = new DefaultTableModel(new String[]{"Product", "Qty", "Unit Price", "Subtotal"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        itemTable = new JTable(itemModel);
        styleTable(itemTable);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(orderTable), new JScrollPane(itemTable));
        split.setResizeWeight(0.6);

        add(topBar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    private void addOrder() {
        List<Supplier> suppliers = supplierService.getAll();
        if (suppliers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suppliers available.");
            return;
        }

        String[] names = new String[suppliers.size()];
        for (int i = 0; i < suppliers.size(); i++) {
            names[i] = suppliers.get(i).getId() + " – " + suppliers.get(i).getName();
        }

        String chosen = (String) JOptionPane.showInputDialog(this, "Select Supplier", 
                "New Order", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);

        if (chosen == null) return;

        try {
            int supplierId = Integer.parseInt(chosen.split(" – ")[0].trim());
            Supplier s = supplierService.getById(supplierId);
            orderService.create(supplierId, s.getName());

            refresh();
            if (onUpdate != null) onUpdate.run();
            if (logPanel != null) logPanel.logActivity("New Order Created", "Supplier: " + s.getName());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to create order.");
        }
    }

    private void addItemToOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order first.");
            return;
        }

        int orderId = parseId(orderModel.getValueAt(row, 0).toString());
        Order order = orderService.getById(orderId);

        if (order == null || order.getStatus() != Order.Status.PENDING) {
            JOptionPane.showMessageDialog(this, "Only PENDING orders can be modified.");
            return;
        }

        List<Product> products = productService.getAll();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products available.");
            return;
        }

        String[] list = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            list[i] = p.getId() + " – " + p.getName();
        }

        String chosen = (String) JOptionPane.showInputDialog(this, "Select Product", 
                "Add Item", JOptionPane.QUESTION_MESSAGE, null, list, list[0]);

        if (chosen == null) return;

        try {
            int productId = Integer.parseInt(chosen.split(" – ")[0].trim());
            String qtyStr = JOptionPane.showInputDialog(this, "Quantity:");
            if (qtyStr == null) return;

            int qty = Integer.parseInt(qtyStr.trim());
            if (qty <= 0) throw new Exception("Quantity must be > 0");

            orderService.addItem(orderId, productId, qty);

            refresh();
            refreshItems();
            if (onUpdate != null) onUpdate.run();
            if (logPanel != null) logPanel.logActivity("Item Added to Order", "Order #" + orderId);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void changeStatus() {
        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        int orderId = parseId(orderModel.getValueAt(row, 0).toString());
        Order order = orderService.getById(orderId);
        if (order == null) return;

        String[] opts = {"PENDING", "COMPLETED", "CANCELLED"};
        String chosen = (String) JOptionPane.showInputDialog(this, "Change Status", 
                "Update Order Status", JOptionPane.QUESTION_MESSAGE, null, opts, order.getStatus().name());

        if (chosen != null) {
            orderService.setStatus(orderId, Order.Status.valueOf(chosen));
            refresh();
            if (onUpdate != null) onUpdate.run();
            if (logPanel != null) logPanel.logActivity("Order Status Changed", "Order #" + orderId + " → " + chosen);
        }
    }

    public void refresh() {
        int selected = -1;
        int row = orderTable.getSelectedRow();
        if (row != -1) {
            selected = parseId(orderModel.getValueAt(row, 0).toString());
        }

        orderModel.setRowCount(0);
        String q = searchField.getText().toLowerCase().trim();

        for (Order o : orderService.getAll()) {
            if (!q.isEmpty() && 
                !o.getSupplierName().toLowerCase().contains(q) && 
                !o.getStatus().name().toLowerCase().contains(q)) {
                continue;
            }

            orderModel.addRow(new Object[]{
                    "#ORD-" + o.getId(),
                    o.getSupplierName(),
                    String.format("$%.2f", o.getTotalAmount()),
                    o.getStatus().name()
            });
        }

        if (selected != -1) {
            for (int i = 0; i < orderModel.getRowCount(); i++) {
                if (parseId(orderModel.getValueAt(i, 0).toString()) == selected) {
                    orderTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }

        refreshItems();
    }

    private void refreshItems() {
        itemModel.setRowCount(0);
        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        int orderId = parseId(orderModel.getValueAt(row, 0).toString());
        Order order = orderService.getById(orderId);
        if (order == null) return;

        for (Order.Item i : order.getItems()) {
            itemModel.addRow(new Object[]{
                    i.getProductName(),
                    i.getQuantity(),
                    String.format("$%.2f", i.getUnitPrice()),
                    String.format("$%.2f", i.getSubtotal())
            });
        }
    }

    private int parseId(String s) {
        try {
            return Integer.parseInt(s.replace("#ORD-", "").trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private void notifyUpdate() {
        if (onUpdate != null) onUpdate.run();
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
        t.setGridColor(new Color(50, 50, 60));
        t.setRowHeight(26);
        t.getTableHeader().setBackground(new Color(40, 40, 50));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(55, 55, 70));
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String s = value == null ? "" : value.toString();
            if ("COMPLETED".equals(s)) setForeground(new Color(34, 197, 94));
            else if ("CANCELLED".equals(s)) setForeground(new Color(239, 68, 68));
            else setForeground(new Color(250, 204, 21));
            return this;
        }
    }
}