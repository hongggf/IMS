package com.inventory.UI.Panel;

import com.inventory.model.ModernButton;
import com.inventory.model.Product;
import com.inventory.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductPanel extends JPanel {

    private final ProductService service;
    private final Runnable onUpdate;

    private final DefaultTableModel model;
    private final JTable table;

    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private DefaultComboBoxModel<String> categoryModel;

    public ProductPanel(ProductService service, Runnable onUpdate) {
        this.service = service;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(18, 18, 22));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Top Bar
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(220, 34));
        styleInput(searchField);
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { refresh(); }
        });

        categoryModel = new DefaultComboBoxModel<>();
        categoryModel.addElement("All");
        categoryModel.addElement("Electronics");
        categoryModel.addElement("Accessories");
        categoryModel.addElement("Furniture");
        categoryModel.addElement("Other");
        categoryModel.addElement("+ Add New");

        categoryBox = new JComboBox<>(categoryModel);
        styleCombo(categoryBox);

        categoryBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if ("+ Add New".equals(categoryBox.getSelectedItem())) {
                    String newCat = JOptionPane.showInputDialog(ProductPanel.this, "Enter new category:");
                    if (newCat != null && !newCat.trim().isEmpty()) {
                        categoryModel.insertElementAt(newCat.trim(), categoryModel.getSize() - 1);
                        categoryBox.setSelectedItem(newCat.trim());
                    }
                } else {
                    refresh();
                }
            }
        });

        ModernButton addBtn = new ModernButton("+ Add", new Color(34, 197, 94));
        ModernButton editBtn = new ModernButton("Edit", new Color(99, 102, 241));
        ModernButton deleteBtn = new ModernButton("Delete", new Color(239, 68, 68));

        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { addProduct(); }
        });
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { editProduct(); }
        });
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { deleteProduct(); }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);
        top.add(searchField);
        top.add(categoryBox);
        top.add(addBtn);
        top.add(editBtn);
        top.add(deleteBtn);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Stock"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        styleTable(table);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    private void addProduct() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(new Color(30, 30, 35));

        form.add(new JLabel("Name")); form.add(nameField);
        form.add(new JLabel("Category")); form.add(categoryBox);
        form.add(new JLabel("Price ($)")); form.add(priceField);
        form.add(new JLabel("Stock")); form.add(stockField);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Product", 
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) throw new Exception("Name cannot be empty");

                String category = categoryBox.getSelectedItem().toString();
                if ("All".equals(category)) category = "Other";

                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());

                if (price <= 0 || stock < 0) throw new Exception("Price must be > 0, Stock >= 0");

                service.add(new Product(0, name, category, price, stock));

                searchField.setText("");
                categoryBox.setSelectedItem("All");

                refresh();
                if (onUpdate != null) onUpdate.run();

                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        Product p = service.getById(id);
        if (p == null) return;

        JTextField nameField = new JTextField(p.getName());
        JTextField priceField = new JTextField(String.valueOf(p.getPrice()));
        JTextField stockField = new JTextField(String.valueOf(p.getStock()));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(new Color(30, 30, 35));

        form.add(new JLabel("Name")); form.add(nameField);
        form.add(new JLabel("Price ($)")); form.add(priceField);
        form.add(new JLabel("Stock")); form.add(stockField);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Product", 
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                p.setName(nameField.getText().trim());
                p.setPrice(Double.parseDouble(priceField.getText().trim()));
                p.setStock(Integer.parseInt(stockField.getText().trim()));

                service.update(p);
                refresh();
                if (onUpdate != null) onUpdate.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage());
            }
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        if (JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm Delete", 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            service.delete(id);
            refresh();
            if (onUpdate != null) onUpdate.run();
        }
    }

    public void refresh() {
        model.setRowCount(0);

        String query = searchField.getText().toLowerCase().trim();
        String selectedCat = (String) categoryBox.getSelectedItem();

        for (Product p : service.getAll()) {
            boolean matchesSearch = query.isEmpty() || 
                p.getName().toLowerCase().contains(query) || 
                p.getCategory().toLowerCase().contains(query);

            boolean matchesCat = "All".equals(selectedCat) || p.getCategory().equals(selectedCat);

            if (matchesSearch && matchesCat) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        String.format("$%.2f", p.getPrice()),
                        p.getStock()
                });
            }
        }
    }

    private void notifyUpdate() {
        if (onUpdate != null) onUpdate.run();
    }

    // ================== CONSISTENT STYLING ==================
    private void styleInput(JTextField f) {
        f.setBackground(new Color(35, 35, 40));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(new Color(35, 35, 40));
        box.setForeground(Color.WHITE);
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(30, 30, 35));
        t.setForeground(Color.LIGHT_GRAY);
        t.setGridColor(new Color(50, 50, 60));
        t.setRowHeight(26);
        t.setSelectionBackground(new Color(55, 55, 70));
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setBackground(new Color(40, 40, 50));
        t.getTableHeader().setForeground(Color.WHITE);
    }
}