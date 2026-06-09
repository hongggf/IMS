package com.inventory.UI;

import com.inventory.Component.Sidebar;
import com.inventory.UI.Panel.*;
import com.inventory.model.ShipmentService;
import com.inventory.service.*;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private final ProductService productService = new ProductService();
    private final SupplierService supplierService = new SupplierService();
    private final OrderService orderService = new OrderService(productService);
    private final ShipmentService shipmentService = new ShipmentService();

    private DashboardPanel dashboardPanel;
    private ProductPanel productPanel;
    private SupplierPanel supplierPanel;
    private OrderPanel orderPanel;
    private ReportPanel reportPanel;
    private StockPanel stockPanel;
    private ShipmentPanel shipmentPanel;
    private ActivityLogPanel logPanel;
    private SystemSettingsPanel settingsPanel;

    public DashboardFrame() {
        setTitle("Inventory Management System");
        setSize(1250, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initPanels();
        initContent();
        initSidebar();

        // Show dashboard by default
        cardLayout.show(contentPanel, "dashboard");
        if (dashboardPanel != null) {
            dashboardPanel.refresh();
        }

        setVisible(true);
    }

    private void initPanels() {
        // LogPanel must be created FIRST
        logPanel = new ActivityLogPanel();

        dashboardPanel = new DashboardPanel(productService, supplierService, new Runnable() {
            public void run() {}
        });

        productPanel = new ProductPanel(productService, new Runnable() {
            public void run() { 
                if (dashboardPanel != null) dashboardPanel.refresh(); 
            }
        });

        // Updated SupplierPanel with logPanel
        supplierPanel = new SupplierPanel(supplierService, orderService, new Runnable() {
            public void run() { 
                if (dashboardPanel != null) dashboardPanel.refresh(); 
            }
        }, logPanel);

        // Updated OrderPanel with logPanel
        orderPanel = new OrderPanel(orderService, supplierService, productService, new Runnable() {
            public void run() {
                if (dashboardPanel != null) dashboardPanel.refresh();
                if (productPanel != null) productPanel.refresh();
                if (supplierPanel != null) supplierPanel.refresh();
            }
        }, logPanel);

        reportPanel = new ReportPanel(productService, new Runnable() {
            public void run() { 
                if (dashboardPanel != null) dashboardPanel.refresh(); 
            }
        });

        stockPanel = new StockPanel(productService);
        
        // ShipmentPanel with logging
        shipmentPanel = new ShipmentPanel(shipmentService, logPanel);

        settingsPanel = new SystemSettingsPanel();
    }

    private void initContent() {
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(productPanel, "products");
        contentPanel.add(supplierPanel, "suppliers");
        contentPanel.add(orderPanel, "orders");
        contentPanel.add(reportPanel, "reports");
        contentPanel.add(stockPanel, "stock");
        contentPanel.add(shipmentPanel, "shipments");
        contentPanel.add(logPanel, "logs");
        contentPanel.add(settingsPanel, "settings");
    }

    private void initSidebar() {
        Sidebar sidebar = new Sidebar();
        sidebar.setNavListener(new Sidebar.NavListener() {
            public void onNavigate(String screen) {
                cardLayout.show(contentPanel, screen);

                if ("dashboard".equals(screen) && dashboardPanel != null) {
                    dashboardPanel.refresh();
                } else if ("products".equals(screen) && productPanel != null) {
                    productPanel.refresh();
                } else if ("suppliers".equals(screen) && supplierPanel != null) {
                    supplierPanel.refresh();
                } else if ("orders".equals(screen) && orderPanel != null) {
                    orderPanel.refresh();
                } else if ("reports".equals(screen) && reportPanel != null) {
                    reportPanel.refresh();
                } else if ("stock".equals(screen) && stockPanel != null) {
                    stockPanel.refresh();
                } else if ("shipments".equals(screen) && shipmentPanel != null) {
                    shipmentPanel.refreshShipments();
                } else if ("logs".equals(screen) && logPanel != null) {
                    logPanel.refresh();
                }
            }
        });

        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
}