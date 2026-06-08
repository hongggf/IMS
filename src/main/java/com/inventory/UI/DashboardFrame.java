package com.inventory.UI;

import com.inventory.Component.Sidebar;
import com.inventory.UI.Panel.*;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    // ================= SERVICES =================
    private final ProductService productService = new ProductService();
    private final SupplierService supplierService = new SupplierService();

    public DashboardFrame() {

        setTitle("Inventory System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ================= SIDEBAR =================
        Sidebar sidebar = new Sidebar();

        // ================= PANELS =================

       final DashboardPanel dashboardPanel =
        new DashboardPanel(productService, supplierService, new Runnable() {
            @Override
            public void run() {
                // optional global sync
            }
        });

        final ProductPanel productPanel =
                new ProductPanel(productService, new Runnable() {
                    @Override
                    public void run() {
                        dashboardPanel.refresh();
                    }
                });

        final SupplierPanel supplierPanel =
                new SupplierPanel(supplierService, new Runnable() {
                    @Override
                    public void run() {
                        dashboardPanel.refresh();
                    }
                });

        final OrderPanel orderPanel =
                new OrderPanel(new Runnable() {
                    @Override
                    public void run() {
                        dashboardPanel.refresh();
                    }
                });

        final ReportPanel reportPanel =
                new ReportPanel(productService, new Runnable() {
                    @Override
                    public void run() {
                        dashboardPanel.refresh();
                    }
                });

        // ================= CARD LAYOUT =================
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(productPanel, "products");
        contentPanel.add(supplierPanel, "suppliers");
        contentPanel.add(orderPanel, "orders");
        contentPanel.add(reportPanel, "reports");

        // ================= NAVIGATION =================
        sidebar.setNavListener(new Sidebar.NavListener() {

            @Override
            public void onNavigate(String screen) {

                if ("dashboard".equals(screen)) {
                    cardLayout.show(contentPanel, "dashboard");
                    dashboardPanel.refresh();
                }

                else if ("products".equals(screen)) {
                    cardLayout.show(contentPanel, "products");
                    productPanel.refresh();
                }

                else if ("suppliers".equals(screen)) {
                    cardLayout.show(contentPanel, "suppliers");
                    supplierPanel.refresh();
                }

                else if ("orders".equals(screen)) {
                    cardLayout.show(contentPanel, "orders");
                    orderPanel.refresh();
                }

                else if ("reports".equals(screen)) {
                    cardLayout.show(contentPanel, "reports");
                    reportPanel.refresh();
                }
            }
        });

        // ================= LAYOUT =================
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}