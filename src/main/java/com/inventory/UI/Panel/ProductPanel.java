package com.inventory.UI.Panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductPanel extends JPanel {

    JTable table;

    public ProductPanel() {

        setLayout(new BorderLayout());

        DefaultTableModel model =
            new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");

        table =
            new JTable(model);

        JScrollPane scroll =
            new JScrollPane(table);

        add(scroll,
            BorderLayout.CENTER);

        model.addRow(
          new Object[]{
            1,
            "Laptop",
            500
          });

        model.addRow(
          new Object[]{
            2,
            "Keyboard",
            50
          });
    }
}