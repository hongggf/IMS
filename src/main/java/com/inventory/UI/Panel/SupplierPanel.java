package com.inventory.UI.Panel;

import com.inventory.model.ModernButton;
import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupplierPanel extends JPanel {

    private final SupplierService service;
    private final Runnable onUpdate;

    private final DefaultTableModel model;
    private final JTable table;

    private JTextField searchField;

    public SupplierPanel(SupplierService service, Runnable onUpdate) {

        this.service = service;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(25, 25, 28));

        model = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Phone", "Email"}, 0);
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

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

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private void addSupplier() {

        JTextField name = new JTextField();
        JTextField contact = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Contact"));
        form.add(contact);
        form.add(new JLabel("Phone"));
        form.add(phone);
        form.add(new JLabel("Email"));
        form.add(email);

        if (JOptionPane.showConfirmDialog(this, form, "Add Supplier", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            service.add(new Supplier(0,
                    name.getText(),
                    contact.getText(),
                    phone.getText(),
                    email.getText(),
                    "Active"
            ));

            refresh();
            notifyUpdate();
        }
    }

    private void editSupplier() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        Supplier s = service.getById(id);
        if (s == null) return;

        JTextField name = new JTextField(s.getName());
        JTextField contact = new JTextField(s.getContact());
        JTextField phone = new JTextField(s.getPhone());
        JTextField email = new JTextField(s.getEmail());

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Contact"));
        form.add(contact);
        form.add(new JLabel("Phone"));
        form.add(phone);
        form.add(new JLabel("Email"));
        form.add(email);

        if (JOptionPane.showConfirmDialog(this, form, "Edit Supplier", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            s.setName(name.getText());
            s.setContact(contact.getText());
            s.setPhone(phone.getText());
            s.setEmail(email.getText());

            service.update(s);

            refresh();
            notifyUpdate();
        }
    }

    private void deleteSupplier() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Delete supplier?");
        if (confirm == JOptionPane.YES_OPTION) {

            service.delete(id);

            refresh();
            notifyUpdate();
        }
    }

    public void refresh() {

        model.setRowCount(0);

        String q = searchField.getText().toLowerCase();

        for (Supplier s : service.getAll()) {

            if (q.isEmpty()
                    || s.getName().toLowerCase().contains(q)
                    || s.getPhone().toLowerCase().contains(q)
                    || s.getEmail().toLowerCase().contains(q)) {

                model.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        s.getContact(),
                        s.getPhone(),
                        s.getEmail()
                });
            }
        }
    }

    private void notifyUpdate() {
        if (onUpdate != null) onUpdate.run();
    }
}