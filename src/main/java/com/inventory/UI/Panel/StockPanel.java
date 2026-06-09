package com.inventory.UI.Panel;

import com.inventory.UI.TableUtils;
import com.inventory.UI.UITheme;
import com.inventory.model.Product;
import com.inventory.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockPanel extends JPanel {

    private final ProductService service;
    private final DefaultTableModel model;
    private final JTable table;

    public StockPanel(ProductService service) {
        this.service = service;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Stock", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        TableUtils.applyTheme(table);

        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        try {
            model.setRowCount(0);
            for (Product p : service.getAll()) {
                String status = p.getStock() == 0 ? "Out of Stock" :
                               p.getStock() < 10 ? "Low" : "OK";

                model.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getStock(),
                        status
                });
            }
        } catch (Exception e) {
            System.err.println("Stock refresh error: " + e.getMessage());
        }
    }
}