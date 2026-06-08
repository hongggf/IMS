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
        setBackground(new Color(25, 25, 28));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================= SEARCH =================
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.addActionListener(e -> refresh());

        // ================= CATEGORY =================
        categoryModel = new DefaultComboBoxModel<>();
        categoryModel.addElement("Electronics");
        categoryModel.addElement("Accessories");
        categoryModel.addElement("Furniture");
        categoryModel.addElement("Other");
        categoryModel.addElement("+ Add New");

        categoryBox = new JComboBox<>(categoryModel);

        categoryBox.addActionListener(e -> {
            if ("+ Add New".equals(categoryBox.getSelectedItem())) {
                String newCat = JOptionPane.showInputDialog(this, "New Category:");
                if (newCat != null && newCat.trim().length() > 0) {
                    categoryModel.insertElementAt(newCat, categoryModel.getSize() - 1);
                    categoryBox.setSelectedItem(newCat);
                }
            }
        });

        // ================= TABLE =================
        model = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Stock"}, 0);
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        // ================= BUTTONS =================
        ModernButton addBtn = new ModernButton("+ Add", new Color(34, 197, 94));
        ModernButton editBtn = new ModernButton("Edit", new Color(99, 102, 241));
        ModernButton deleteBtn = new ModernButton("Delete", new Color(220, 53, 69));

        addBtn.addActionListener(e -> addProduct());
        editBtn.addActionListener(e -> editProduct());
        deleteBtn.addActionListener(e -> deleteProduct());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(searchField);
        top.add(addBtn);
        top.add(editBtn);
        top.add(deleteBtn);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private void addProduct() {

        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JTextField stock = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Category"));
        form.add(categoryBox);
        form.add(new JLabel("Price"));
        form.add(price);
        form.add(new JLabel("Stock"));
        form.add(stock);

        if (JOptionPane.showConfirmDialog(this, form, "Add Product", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

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
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Price"));
        form.add(price);
        form.add(new JLabel("Stock"));
        form.add(stock);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Product", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

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

        String q = searchField.getText().toLowerCase();

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
}