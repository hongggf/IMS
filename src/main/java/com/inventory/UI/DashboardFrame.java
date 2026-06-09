package com.inventory.UI;

import com.inventory.Component.Sidebar;
import com.inventory.UI.Panel.*;
import com.inventory.service.OrderService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;
import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    // Keep these as fields
    private final ProductService productService;
    private final SupplierService supplierService;
    private final OrderService orderService;

    public DashboardFrame() {
        // 1. Initialize services first
        this.productService = new ProductService();
        this.supplierService = new SupplierService();
        this.orderService = new OrderService(productService);

        setTitle("Inventory System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. Initialize Panels
        DashboardPanel dashboardPanel = new DashboardPanel(productService, supplierService, () -> {});
        ProductPanel productPanel = new ProductPanel(productService, dashboardPanel::refresh);
        SupplierPanel supplierPanel = new SupplierPanel(supplierService, orderService, dashboardPanel::refresh);
        
        OrderPanel orderPanel = new OrderPanel(orderService, supplierService, productService, () -> {
            dashboardPanel.refresh();
            productPanel.refresh();
            supplierPanel.refresh();
        });

        ReportPanel reportPanel = new ReportPanel(productService, dashboardPanel::refresh);

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(productPanel, "products");
        contentPanel.add(supplierPanel, "suppliers");
        contentPanel.add(orderPanel, "orders");
        contentPanel.add(reportPanel, "reports");

        Sidebar sidebar = new Sidebar();
        sidebar.setNavListener(screen -> {
            cardLayout.show(contentPanel, screen);
            // Refresh ONLY the active panel to prevent unnecessary errors
            switch (screen) {
                case "dashboard": dashboardPanel.refresh(); break;
                case "products": productPanel.refresh(); break;
                case "suppliers": supplierPanel.refresh(); break;
                case "orders": orderPanel.refresh(); break;
                case "reports": reportPanel.refresh(); break;
            }
        });

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        // 3. IMPORTANT: Refresh everything ONCE here, after everything is built
        dashboardPanel.refresh();
        
        setVisible(true);
    }
}