package com.inventory.UI.Panel;

import com.inventory.model.ModernButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OrderPanel extends JPanel {

    private final Runnable onUpdate;

    private final DefaultTableModel model;
    private final JTable table;

    private JTextField searchField;

    public OrderPanel(Runnable onUpdate) {

        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(25, 25, 28));

        model = new DefaultTableModel(new String[]{"Order ID", "Supplier", "Total", "Status"}, 0);
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.addActionListener(e -> refresh());

        ModernButton addBtn = new ModernButton("+ Add", new Color(34, 197, 94));
        ModernButton statusBtn = new ModernButton("Status", new Color(99, 102, 241));

        addBtn.addActionListener(e -> addOrder());
        statusBtn.addActionListener(e -> changeStatus());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(searchField);
        top.add(addBtn);
        top.add(statusBtn);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private void addOrder() {

        JTextField supplier = new JTextField();
        JTextField total = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("Supplier"));
        form.add(supplier);
        form.add(new JLabel("Total"));
        form.add(total);

        if (JOptionPane.showConfirmDialog(this, form, "New Order", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            model.addRow(new Object[]{
                    "#ORD-" + (model.getRowCount() + 1),
                    supplier.getText(),
                    total.getText(),
                    "Pending"
            });

            notifyUpdate();
        }
    }

    private void changeStatus() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        String[] options = {"Pending", "Completed", "Cancelled"};

        String status = (String) JOptionPane.showInputDialog(
                this,
                "Change Status",
                "Order Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                model.getValueAt(row, 3)
        );

        if (status != null) {
            model.setValueAt(status, row, 3);
            notifyUpdate();
        }
    }

    public void refresh() {
        model.setRowCount(model.getRowCount());
    }

    private void notifyUpdate() {
        if (onUpdate != null) onUpdate.run();
    }
}