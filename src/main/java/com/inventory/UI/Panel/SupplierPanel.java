package com.inventory.UI.Panel;

import com.inventory.model.ModernButton;
import com.inventory.model.Order;
import com.inventory.model.Supplier;
import com.inventory.service.OrderService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierPanel extends JPanel {

    private final SupplierService service;
    private final OrderService    orderService;
    private final Runnable        onUpdate;

    // ── Top table: suppliers ──────────────────────────────────────────
    private final DefaultTableModel supplierModel;
    private final JTable            supplierTable;

    // ── Bottom table: orders for selected supplier ────────────────────
    private final DefaultTableModel ordersModel;
    private final JTable            ordersTable;

    private JTextField searchField;

    public SupplierPanel(SupplierService service,
                         OrderService orderService,
                         Runnable onUpdate) {

        this.service      = service;
        this.orderService = orderService;
        this.onUpdate     = onUpdate;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(25, 25, 28));

        // ── Supplier table ────────────────────────────────────────────
        supplierModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Contact", "Phone", "Email"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        supplierTable = new JTable(supplierModel);
        styleTable(supplierTable);
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshOrders();
        });

        JScrollPane supplierScroll = new JScrollPane(supplierTable);
        supplierScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                "Suppliers", 0, 0, null, Color.LIGHT_GRAY));

        // ── Orders sub-table ──────────────────────────────────────────
        ordersModel = new DefaultTableModel(
                new String[]{"Order ID", "Total", "# Items", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        styleTable(ordersTable);

        // Colour-coded Status column
        ordersTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
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

        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70)),
                "Orders for selected supplier", 0, 0, null, Color.LIGHT_GRAY));

        // ── Toolbar ───────────────────────────────────────────────────
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.addActionListener(e -> refresh());

        ModernButton addBtn    = new ModernButton("+ Add",  new Color(34, 197, 94));
        ModernButton editBtn   = new ModernButton("Edit",   new Color(99, 102, 241));
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

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, supplierScroll, ordersScroll);
        split.setResizeWeight(0.55);
        split.setBackground(new Color(25, 25, 28));
        split.setBorder(null);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    // ─────────────────────────────────────────────────────────────────
    // SUPPLIER CRUD
    // ─────────────────────────────────────────────────────────────────
    private void addSupplier() {

        JTextField name    = new JTextField();
        JTextField contact = new JTextField();
        JTextField phone   = new JTextField();
        JTextField email   = new JTextField();

        JPanel form = buildForm(name, contact, phone, email);

        if (JOptionPane.showConfirmDialog(this, form, "Add Supplier", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            service.add(new Supplier(0,
                    name.getText(), contact.getText(),
                    phone.getText(), email.getText(), "Active"));

            refresh();
            notifyUpdate();
        }
    }

    private void editSupplier() {

        int row = supplierTable.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(supplierModel.getValueAt(row, 0).toString());
        Supplier s = service.getById(id);
        if (s == null) return;

        JTextField name    = new JTextField(s.getName());
        JTextField contact = new JTextField(s.getContact());
        JTextField phone   = new JTextField(s.getPhone());
        JTextField email   = new JTextField(s.getEmail());

        JPanel form = buildForm(name, contact, phone, email);

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

        int row = supplierTable.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(supplierModel.getValueAt(row, 0).toString());

        if (JOptionPane.showConfirmDialog(this, "Delete supplier?") == JOptionPane.YES_OPTION) {
            service.delete(id);
            refresh();
            notifyUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // REFRESH
    // ─────────────────────────────────────────────────────────────────
    public void refresh() {

        // remember selection
        int selectedId = -1;
        int selRow = supplierTable.getSelectedRow();
        if (selRow != -1)
            selectedId = Integer.parseInt(supplierModel.getValueAt(selRow, 0).toString());

        supplierModel.setRowCount(0);
        String q = searchField.getText().toLowerCase();

        for (Supplier s : service.getAll()) {
            if (!q.isEmpty()
                    && !s.getName().toLowerCase().contains(q)
                    && !s.getPhone().toLowerCase().contains(q)
                    && !s.getEmail().toLowerCase().contains(q)) continue;

            supplierModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getContact(), s.getPhone(), s.getEmail()
            });
        }

        // restore selection
        if (selectedId != -1) {
            for (int r = 0; r < supplierModel.getRowCount(); r++) {
                if (Integer.parseInt(supplierModel.getValueAt(r, 0).toString()) == selectedId) {
                    supplierTable.setRowSelectionInterval(r, r);
                    break;
                }
            }
        }

        refreshOrders();
    }

    private void refreshOrders() {

        ordersModel.setRowCount(0);
        int selRow = supplierTable.getSelectedRow();
        if (selRow == -1) return;

        int supplierId = Integer.parseInt(supplierModel.getValueAt(selRow, 0).toString());

        for (Order o : orderService.getBySupplierId(supplierId)) {
            ordersModel.addRow(new Object[]{
                    "#ORD-" + o.getId(),
                    String.format("$%.2f", o.getTotal()),
                    o.getItems().size(),
                    o.getStatus().name()
            });
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────
    private JPanel buildForm(JTextField name, JTextField contact,
                              JTextField phone, JTextField email) {
        JPanel f = new JPanel(new GridLayout(0, 1, 4, 4));
        f.add(new JLabel("Name"));    f.add(name);
        f.add(new JLabel("Contact")); f.add(contact);
        f.add(new JLabel("Phone"));   f.add(phone);
        f.add(new JLabel("Email"));   f.add(email);
        return f;
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