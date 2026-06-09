package com.inventory.UI.Panel;

import com.inventory.Component.StatCard;
import com.inventory.model.Product;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final ProductService productService;
    private final SupplierService supplierService;
    private final Runnable onUpdate;

    private final StatCard cardProducts;
    private final StatCard cardLow;
    private final StatCard cardSuppliers;
    private final StatCard cardValue;

    private final DefaultTableModel tableModel;
    private final JTable table;

    public DashboardPanel(ProductService productService,
                          SupplierService supplierService,
                          Runnable onUpdate) {

        this.productService = productService;
        this.supplierService = supplierService;
        this.onUpdate = onUpdate;

        setBackground(new Color(18, 18, 20));
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        JLabel title = new JLabel("Dashboard Overview");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Live inventory insights & activity");
        subtitle.setForeground(new Color(150, 150, 160));
        header.add(title);
        header.add(subtitle);

        // Stats
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);

        cardProducts = new StatCard("Total Products", "0");
        cardLow = new StatCard("Low Stock", "0");
        cardSuppliers = new StatCard("Suppliers", "0");
        cardValue = new StatCard("Inventory Value", "$0");

        stats.add(cardProducts);
        stats.add(cardLow);
        stats.add(cardSuppliers);
        stats.add(cardValue);

        // Recent Products Table
        String[] cols = {"ID", "Name", "Category", "Price", "Stock", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(28, 28, 32));

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(new JLabel("Recent Products") {{
            setForeground(Color.LIGHT_GRAY);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
        }}, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        JPanel middle = new JPanel(new BorderLayout(0, 16));
        middle.setOpaque(false);
        middle.add(stats, BorderLayout.NORTH);
        middle.add(center, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        List<Product> products = productService.getAll();

        cardProducts.setValue(String.valueOf(products.size()));
        cardLow.setValue(String.valueOf(productService.countLowStock()));
        cardValue.setValue(String.format("$%,.0f", productService.totalValue()));
        cardSuppliers.setValue(String.valueOf(supplierService.getAll().size()));

        tableModel.setRowCount(0);
        int limit = Math.min(8, products.size());
        for (int i = products.size() - limit; i < products.size(); i++) {
            Product p = products.get(i);
            tableModel.addRow(new Object[]{
                    "#" + p.getId(),
                    p.getName(),
                    p.getCategory(),
                    String.format("$%.2f", p.getPrice()),
                    p.getStock(),
                    p.getStatus()
            });
        }
        if (onUpdate != null) onUpdate.run();
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(28, 28, 32));
        t.setForeground(Color.WHITE);
        t.setRowHeight(38);
        t.setShowGrid(false);
        t.getTableHeader().setBackground(new Color(35, 35, 45));
        t.getTableHeader().setForeground(Color.WHITE);
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        
        String status = value == null ? "" : value.toString().trim();

        if ("Out of Stock".equals(status) || "Low".equals(status)) {
            label.setBackground(new Color(80, 35, 35));     // Dark Red
            label.setForeground(new Color(255, 100, 100));  // Bright Red
        } 
        else if ("OK".equals(status)) {
            label.setBackground(new Color(40, 80, 50));     // Dark Green
            label.setForeground(new Color(120, 255, 160));  // Light Green
        } 
        else {
            label.setBackground(new Color(55, 55, 70));
            label.setForeground(Color.WHITE);
        }

        label.setHorizontalAlignment(CENTER);
        label.setOpaque(true);
        return label;
    }
}
}