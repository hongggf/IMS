package com.inventory.UI;

import javax.swing.JPanel;

import com.inventory.Component.Sidebar;
import com.inventory.UI.Panel.DashboardPanel;
import com.inventory.UI.Panel.ProductPanel;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public DashboardFrame() {

        setTitle("Inventory System");

        setSize(1400,800);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Sidebar sidebar =
                new Sidebar();

        cardLayout =
                new CardLayout();

        contentPanel =
                new JPanel(cardLayout);

        contentPanel.add(
            new DashboardPanel(),
            "dashboard");

        contentPanel.add(
            new ProductPanel(),
            "products");

        sidebar.dashboardBtn
               .addActionListener(
                e -> cardLayout.show(
                    contentPanel,
                    "dashboard"));

        sidebar.productBtn
               .addActionListener(
                e -> cardLayout.show(
                    contentPanel,
                    "products"));

        add(sidebar,BorderLayout.WEST);

        add(contentPanel,
            BorderLayout.CENTER);

        setVisible(true);
    }
}
