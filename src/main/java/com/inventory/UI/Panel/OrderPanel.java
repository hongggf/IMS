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

    // ── TABLES ─────────────────────────────
    private final DefaultTableModel orderModel;
    private final JTable orderTable;

    private final DefaultTableModel itemModel;
    private final JTable itemTable;

    private JTextField searchField;

    public OrderPanel(OrderService orderService,
                      SupplierService supplierService,
                      ProductService productService,
                      Runnable onUpdate) {

        this.orderService = orderService;
        this.supplierService = supplierService;
        this.productService = productService;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(25, 25, 28));

        // ── HEADER (modern toolbar) ─────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(180, 32));
        searchField.setBackground(new Color(35, 35, 40));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);

        searchField.addActionListener(e -> refresh());

        ModernButton addOrderBtn = new ModernButton("+ Order", new Color(34, 197, 94));
        ModernButton addItemBtn  = new ModernButton("+ Item", new Color(59, 130, 246));
        ModernButton statusBtn   = new ModernButton("Status", new Color(99, 102, 241));

        addOrderBtn.addActionListener(e -> addOrder());
        addItemBtn.addActionListener(e -> addItemToOrder());
        statusBtn.addActionListener(e -> changeStatus());

        topBar.add(searchField);
        topBar.add(addOrderBtn);
        topBar.add(addItemBtn);
        topBar.add(statusBtn);

        // ── ORDER TABLE ─────────────────────────────
        orderModel = new DefaultTableModel(
                new String[]{"Order ID", "Supplier", "Total", "Status"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(orderModel);
        styleTable(orderTable);

        // Status coloring
        orderTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);

                String s = v == null ? "" : v.toString();

                if ("COMPLETED".equals(s)) setForeground(new Color(34, 197, 94));
                else if ("CANCELLED".equals(s)) setForeground(new Color(239, 68, 68));
                else setForeground(new Color(250, 204, 21));

                setBackground(sel ? new Color(55, 55, 60) : new Color(30, 30, 35));
                return this;
            }
        });

        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshItems();
        });

        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                "Orders", 0, 0, null, Color.LIGHT_GRAY
        ));

        // ── ITEM TABLE ─────────────────────────────
        itemModel = new DefaultTableModel(
                new String[]{"Product", "Qty", "Unit Price", "Subtotal"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        itemTable = new JTable(itemModel);
        styleTable(itemTable);

        JScrollPane itemScroll = new JScrollPane(itemTable);
        itemScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                "Order Items", 0, 0, null, Color.LIGHT_GRAY
        ));

        // ── SPLIT VIEW ─────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderScroll, itemScroll);
        split.setResizeWeight(0.6);
        split.setBorder(null);
        split.setBackground(new Color(25, 25, 28));

        add(topBar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    // ─────────────────────────────────────────────
    // CORE FUNCTIONS (UNCHANGED LOGIC)
    // ─────────────────────────────────────────────

    private void addOrder() {

        List<Supplier> suppliers = supplierService.getAll();
        if (suppliers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suppliers available.");
            return;
        }

        String[] names = suppliers.stream()
                .map(s -> s.getId() + " – " + s.getName())
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                this, "Select Supplier", "New Order",
                JOptionPane.QUESTION_MESSAGE, null,
                names, names[0]);

        if (chosen == null) return;

        int supplierId = Integer.parseInt(chosen.split(" – ")[0].trim());
        Supplier s = supplierService.getById(supplierId);

        if (s != null) {
            orderService.create(supplierId, s.getName());
            refresh();
            notifyUpdate();
        }
    }

    private void addItemToOrder() {

        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select order first.");
            return;
        }

        int orderId = parseId(orderModel.getValueAt(row, 0).toString());
        Order order = orderService.getById(orderId);

        if (order == null || order.getStatus() != Order.Status.PENDING) {
            JOptionPane.showMessageDialog(this, "Only PENDING orders allowed.");
            return;
        }

        List<Product> products = productService.getAll();

        String[] list = products.stream()
                .map(p -> p.getId() + " – " + p.getName() + " ($" + p.getPrice() + ")")
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                this, "Select Product", "Add Item",
                JOptionPane.QUESTION_MESSAGE, null,
                list, list[0]);

        if (chosen == null) return;

        int productId = Integer.parseInt(chosen.split(" – ")[0].trim());

        String qtyStr = JOptionPane.showInputDialog(this, "Quantity:");
        if (qtyStr == null) return;

        try {
            int qty = Integer.parseInt(qtyStr.trim());
            orderService.addItem(orderId, productId, qty);

            refresh();
            refreshItems();
            notifyUpdate();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity");
        }
    }

    private void changeStatus() {

        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        int orderId = parseId(orderModel.getValueAt(row, 0).toString());
        Order order = orderService.getById(orderId);

        String[] opts = {"PENDING", "COMPLETED", "CANCELLED"};

        String chosen = (String) JOptionPane.showInputDialog(
                this, "Change Status", "Status",
                JOptionPane.QUESTION_MESSAGE, null,
                opts, order.getStatus().name());

        if (chosen != null) {
            orderService.setStatus(orderId, Order.Status.valueOf(chosen));
            refresh();
            notifyUpdate();
        }
    }

    public void refresh() {

        int selected = -1;
        int row = orderTable.getSelectedRow();
        if (row != -1)
            selected = parseId(orderModel.getValueAt(row, 0).toString());

        orderModel.setRowCount(0);

        String q = searchField.getText().toLowerCase().trim();

        for (Order o : orderService.getAll()) {

            if (!q.isEmpty() &&
                    !o.getSupplierName().toLowerCase().contains(q) &&
                    !o.getStatus().name().toLowerCase().contains(q)) continue;

            orderModel.addRow(new Object[]{
                    "#ORD-" + o.getId(),
                    o.getSupplierName(),
                    String.format("$%.2f", o.getTotal()),
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

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────

    private int parseId(String s) {
        return Integer.parseInt(s.replace("#ORD-", "").trim());
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(30, 30, 35));
        t.setForeground(Color.LIGHT_GRAY);
        t.setGridColor(new Color(50, 50, 60));
        t.setRowHeight(26);
        t.getTableHeader().setBackground(new Color(40, 40, 50));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(55, 55, 70));
        t.setSelectionForeground(Color.WHITE);
    }

    private void notifyUpdate() {
        if (onUpdate != null) onUpdate.run();
    }
}