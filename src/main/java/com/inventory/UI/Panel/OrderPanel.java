package com.inventory.UI.Panel;

import com.inventory.model.ModernButton;
import com.inventory.model.Order;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.service.OrderService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {

    private final OrderService   orderService;
    private final SupplierService supplierService;
    private final ProductService  productService;
    private final Runnable        onUpdate;

    // ── Top table: orders ─────────────────────────────────────────────
    private final DefaultTableModel orderModel;
    private final JTable            orderTable;

    // ── Bottom table: items of selected order ─────────────────────────
    private final DefaultTableModel itemModel;
    private final JTable            itemTable;

    private JTextField searchField;

    public OrderPanel(OrderService orderService,
                      SupplierService supplierService,
                      ProductService productService,
                      Runnable onUpdate) {

        this.orderService    = orderService;
        this.supplierService = supplierService;
        this.productService  = productService;
        this.onUpdate        = onUpdate;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 28));

        // ── Order table ───────────────────────────────────────────────
        orderModel = new DefaultTableModel(
                new String[]{"Order ID", "Supplier", "Total", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        styleTable(orderTable);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshItems();
        });

        // Status column: colour-code the cell
        orderTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String status = v == null ? "" : v.toString();
                if ("COMPLETED".equals(status)) {
                    setForeground(new Color(34, 197, 94));
                } else if ("CANCELLED".equals(status)) {
                    setForeground(new Color(239, 68, 68));
                } else {
                    setForeground(new Color(250, 204, 21));
                }
                if (sel) setBackground(new Color(55, 55, 60));
                else     setBackground(new Color(30, 30, 35));
                return this;
            }
        });

        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                "Orders", 0, 0, null, Color.LIGHT_GRAY));

        // ── Item table ────────────────────────────────────────────────
        itemModel = new DefaultTableModel(
                new String[]{"Product", "Qty", "Unit Price", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        itemTable = new JTable(itemModel);
        styleTable(itemTable);

        JScrollPane itemScroll = new JScrollPane(itemTable);
        itemScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                "Order Items (select an order above)", 0, 0, null, Color.LIGHT_GRAY));

        // ── Toolbar ───────────────────────────────────────────────────
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(180, 32));
        searchField.addActionListener(e -> refresh());

        ModernButton addBtn    = new ModernButton("+ New Order",  new Color(34, 197, 94));
        ModernButton addItemBtn= new ModernButton("+ Add Item",   new Color(59, 130, 246));
        ModernButton statusBtn = new ModernButton("Change Status",new Color(99, 102, 241));

        addBtn.addActionListener(e -> addOrder());
        addItemBtn.addActionListener(e -> addItemToOrder());
        statusBtn.addActionListener(e -> changeStatus());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(searchField);
        top.add(addBtn);
        top.add(addItemBtn);
        top.add(statusBtn);

        // ── Layout: split orders (top 60%) / items (bottom 40%) ───────
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderScroll, itemScroll);
        split.setResizeWeight(0.6);
        split.setBackground(new Color(25, 25, 28));
        split.setBorder(null);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    // ─────────────────────────────────────────────────────────────────
    // ADD ORDER
    // ─────────────────────────────────────────────────────────────────
    private void addOrder() {

        List<Supplier> suppliers = supplierService.getAll();
        if (suppliers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suppliers available. Please add a supplier first.");
            return;
        }

        String[] supplierNames = suppliers.stream()
                .map(s -> s.getId() + " – " + s.getName())
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                this, "Select Supplier:", "New Order",
                JOptionPane.QUESTION_MESSAGE, null,
                supplierNames, supplierNames[0]);

        if (chosen == null) return;

        // parse "id – name"
        int supplierId = Integer.parseInt(chosen.split(" – ")[0].trim());
        Supplier supplier = supplierService.getById(supplierId);
        if (supplier == null) return;

        orderService.create(supplierId, supplier.getName());
        refresh();
        notifyUpdate();
    }

    // ─────────────────────────────────────────────────────────────────
    // ADD ITEM TO SELECTED ORDER
    // ─────────────────────────────────────────────────────────────────
    private void addItemToOrder() {

        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        int orderId = parseOrderId(orderModel.getValueAt(row, 0).toString());
        Order order = orderService.getById(orderId);
        if (order == null) return;

        if (order.getStatus() != Order.Status.PENDING) {
            JOptionPane.showMessageDialog(this, "Can only add items to PENDING orders.");
            return;
        }

        List<Product> products = productService.getAll();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products available.");
            return;
        }

        String[] productLabels = products.stream()
                .map(p -> p.getId() + " – " + p.getName() + " ($" + p.getPrice() + ")")
                .toArray(String[]::new);

        String chosenProd = (String) JOptionPane.showInputDialog(
                this, "Select Product:", "Add Item",
                JOptionPane.QUESTION_MESSAGE, null,
                productLabels, productLabels[0]);

        if (chosenProd == null) return;

        int productId = Integer.parseInt(chosenProd.split(" – ")[0].trim());

        String qtyStr = JOptionPane.showInputDialog(this, "Quantity:");
        if (qtyStr == null || qtyStr.trim().isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyStr.trim());
            if (qty <= 0) throw new NumberFormatException();
            orderService.addItem(orderId, productId, qty);
            refresh();
            refreshItems();
            notifyUpdate();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // CHANGE STATUS
    // ─────────────────────────────────────────────────────────────────
    private void changeStatus() {

        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        int orderId = parseOrderId(orderModel.getValueAt(row, 0).toString());
        Order order = orderService.getById(orderId);
        if (order == null) return;

        String[] options = {"PENDING", "COMPLETED", "CANCELLED"};

        String chosen = (String) JOptionPane.showInputDialog(
                this, "Change Status", "Order Status",
                JOptionPane.QUESTION_MESSAGE, null,
                options, order.getStatus().name());

        if (chosen != null) {
            orderService.setStatus(orderId, Order.Status.valueOf(chosen));
            refresh();
            notifyUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // REFRESH
    // ─────────────────────────────────────────────────────────────────
    public void refresh() {

        // remember selection
        int selectedOrderId = -1;
        int selRow = orderTable.getSelectedRow();
        if (selRow != -1)
            selectedOrderId = parseOrderId(orderModel.getValueAt(selRow, 0).toString());

        orderModel.setRowCount(0);
        String q = searchField.getText().toLowerCase().trim();

        for (Order o : orderService.getAll()) {
            if (!q.isEmpty() && !o.getSupplierName().toLowerCase().contains(q)
                    && !o.getStatus().name().toLowerCase().contains(q)) continue;

            orderModel.addRow(new Object[]{
                    "#ORD-" + o.getId(),
                    o.getSupplierName(),
                    String.format("$%.2f", o.getTotal()),
                    o.getStatus().name()
            });
        }

        // restore selection
        if (selectedOrderId != -1) {
            for (int r = 0; r < orderModel.getRowCount(); r++) {
                if (parseOrderId(orderModel.getValueAt(r, 0).toString()) == selectedOrderId) {
                    orderTable.setRowSelectionInterval(r, r);
                    break;
                }
            }
        }

        refreshItems();
    }

    private void refreshItems() {

        itemModel.setRowCount(0);
        int selRow = orderTable.getSelectedRow();
        if (selRow == -1) return;

        int orderId = parseOrderId(orderModel.getValueAt(selRow, 0).toString());
        Order order = orderService.getById(orderId);
        if (order == null) return;

        for (Order.Item item : order.getItems()) {
            itemModel.addRow(new Object[]{
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("$%.2f", item.getUnitPrice()),
                    String.format("$%.2f", item.getSubtotal())
            });
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────
    private int parseOrderId(String cell) {
        return Integer.parseInt(cell.replace("#ORD-", "").trim());
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