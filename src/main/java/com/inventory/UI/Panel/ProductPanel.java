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

        // ───────────────────────── TOP BAR ─────────────────────────
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(220, 34));
        styleInput(searchField);
        searchField.addActionListener(e -> refresh());

        categoryModel = new DefaultComboBoxModel<>();
        categoryModel.addElement("Electronics");
        categoryModel.addElement("Accessories");
        categoryModel.addElement("Furniture");
        categoryModel.addElement("Other");
        categoryModel.addElement("+ Add New");

        categoryBox = new JComboBox<>(categoryModel);
        styleCombo(categoryBox);

        categoryBox.addActionListener(e -> {
            if ("+ Add New".equals(categoryBox.getSelectedItem())) {
                String newCat = JOptionPane.showInputDialog(this, "New Category:");
                if (newCat != null && !newCat.trim().isEmpty()) {
                    categoryModel.insertElementAt(newCat, categoryModel.getSize() - 1);
                    categoryBox.setSelectedItem(newCat);
                }
            }
        });

        ModernButton addBtn = new ModernButton("+ Add", new Color(34, 197, 94));
        ModernButton editBtn = new ModernButton("Edit", new Color(99, 102, 241));
        ModernButton deleteBtn = new ModernButton("Delete", new Color(239, 68, 68));

        addBtn.addActionListener(e -> addProduct());
        editBtn.addActionListener(e -> editProduct());
        deleteBtn.addActionListener(e -> deleteProduct());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setOpaque(false);

        top.add(searchField);
        top.add(categoryBox);
        top.add(addBtn);
        top.add(editBtn);
        top.add(deleteBtn);

        // ───────────────────────── TABLE ─────────────────────────
        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Category", "Price", "Stock"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 50)));

        // ───────────────────────── LAYOUT ─────────────────────────
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    // ───────────────────────── LOGIC (UNCHANGED) ─────────────────────────
    private void addProduct() {

        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JTextField stock = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.setBackground(new Color(30, 30, 35));

        form.add(label("Name"));
        form.add(name);
        form.add(label("Category"));
        form.add(categoryBox);
        form.add(label("Price"));
        form.add(price);
        form.add(label("Stock"));
        form.add(stock);

        if (JOptionPane.showConfirmDialog(this, form, "Add Product",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            service.add(new Product(
                    0,
                    name.getText(),
                    categoryBox.getSelectedItem().toString(),
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText())
            ));

            refresh();
            notifyUpdate();
        }
    }

    private void editProduct() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        Product p = service.getById(id);
        if (p == null) return;

        JTextField name = new JTextField(p.getName());
        JTextField price = new JTextField(String.valueOf(p.getPrice()));
        JTextField stock = new JTextField(String.valueOf(p.getStock()));

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.setBackground(new Color(30, 30, 35));

        form.add(label("Name"));
        form.add(name);
        form.add(label("Price"));
        form.add(price);
        form.add(label("Stock"));
        form.add(stock);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Product",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            p.setName(name.getText());
            p.setPrice(Double.parseDouble(price.getText()));
            p.setStock(Integer.parseInt(stock.getText()));

            service.update(p);

            refresh();
            notifyUpdate();
        }
    }

    private void deleteProduct() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        service.delete(id);

        refresh();
        notifyUpdate();
    }

    public void refresh() {

        model.setRowCount(0);

        String q = searchField.getText().toLowerCase().trim();

        for (Product p : service.getAll()) {

            if (q.isEmpty()
                    || p.getName().toLowerCase().contains(q)
                    || p.getCategory().toLowerCase().contains(q)) {

                model.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getPrice(),
                        p.getStock()
                });
            }
        }
    }

    private void notifyUpdate() {
        if (onUpdate != null) onUpdate.run();
    }

    // ───────────────────────── UI HELPERS ─────────────────────────

    private void styleInput(JTextField f) {
        f.setBackground(new Color(25, 25, 30));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 45, 55)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(new Color(25, 25, 30));
        box.setForeground(Color.WHITE);
        box.setFocusable(false);
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(28, 28, 34));
        t.setForeground(Color.LIGHT_GRAY);
        t.setGridColor(new Color(45, 45, 55));
        t.setRowHeight(26);
        t.setSelectionBackground(new Color(55, 55, 70));
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setBackground(new Color(35, 35, 45));
        t.getTableHeader().setForeground(Color.WHITE);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 180, 180));
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }
}