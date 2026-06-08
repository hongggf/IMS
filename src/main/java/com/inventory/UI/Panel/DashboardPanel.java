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

    private static final Color BG = new Color(18, 18, 20);
    private static final Color PANEL = new Color(28, 28, 32);

    public DashboardPanel(ProductService productService,
                          SupplierService supplierService,
                          Runnable onUpdate) {

        this.productService = productService;
        this.supplierService = supplierService;
        this.onUpdate = onUpdate;

        setBackground(BG);
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        // ================= HEADER =================
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);

        JLabel title = new JLabel("Dashboard Overview");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Live inventory insights & activity");
        subtitle.setForeground(new Color(150, 150, 160));

        header.add(title);
        header.add(subtitle);

        // ================= STATS =================
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);

        cardProducts = new StatCard("Products", "0");
        cardLow = new StatCard("Low Stock", "0");
        cardSuppliers = new StatCard("Suppliers", "0");
        cardValue = new StatCard("Total Value", "$0");

        stats.add(cardProducts);
        stats.add(cardLow);
        stats.add(cardSuppliers);
        stats.add(cardValue);

        // ================= TABLE =================
        String[] cols = {"ID", "Name", "Category", "Price", "Stock", "Status"};

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setBackground(PANEL);
        table.setForeground(Color.WHITE);
        table.setRowHeight(38);
        table.setShowGrid(false);

        // STATUS RENDERER
        table.getColumnModel().getColumn(5).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {

                        JLabel lbl = new JLabel(val == null ? "" : val.toString());
                        lbl.setOpaque(true);
                        lbl.setHorizontalAlignment(CENTER);

                        String s = val == null ? "" : val.toString();

                        if ("OK".equals(s)) {
                            lbl.setBackground(new Color(40, 80, 50));
                            lbl.setForeground(new Color(120, 255, 160));
                        } else if ("Low".equals(s)) {
                            lbl.setBackground(new Color(80, 65, 25));
                            lbl.setForeground(new Color(255, 200, 90));
                        } else {
                            lbl.setBackground(new Color(80, 35, 35));
                            lbl.setForeground(new Color(255, 110, 110));
                        }

                        return lbl;
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(PANEL);

        // ================= CENTER =================
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);

        JLabel recentLabel = new JLabel("Recent Products");
        recentLabel.setForeground(Color.LIGHT_GRAY);
        recentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        center.add(recentLabel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        JPanel middle = new JPanel(new BorderLayout(0, 16));
        middle.setOpaque(false);
        middle.add(stats, BorderLayout.NORTH);
        middle.add(center, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);

        refresh();
    }

    // ================= REFRESH =================
    public void refresh() {

        List<Product> all = productService.getAll();

        cardProducts.setValue(String.valueOf(all.size()));
        cardLow.setValue(String.valueOf(productService.countLowStock()));
        cardValue.setValue(String.format("$%,.0f", productService.totalValue()));

        cardSuppliers.setValue(String.valueOf(supplierService.getAll().size()));

        tableModel.setRowCount(0);

        int start = Math.max(0, all.size() - 6);

        for (int i = all.size() - 1; i >= start; i--) {

            Product p = all.get(i);

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
}