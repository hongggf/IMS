package com.inventory.service;

import com.inventory.core.AppEventBus;
import com.inventory.model.Order;
import com.inventory.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final List<Order>   orders  = new ArrayList<>();
    private final ProductService productService;
    private int nextId = 1;

    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    // ── CRUD ──────────────────────────────────────────────────────────

    public Order create(int supplierId, String supplierName) {
        Order order = new Order(nextId++, supplierId, supplierName);
        orders.add(order);
        AppEventBus.publish();
        return order;
    }

    public void addItem(int orderId, int productId, int quantity) {
        Order order = getById(orderId);
        if (order == null) return;

        Product p = productService.getById(productId);
        if (p == null) return;

        order.addItem(new Order.Item(productId, p.getName(), quantity, p.getPrice()));
        AppEventBus.publish();
    }

    /**
     * Change order status.
     * COMPLETED  → deducts stock from products.
     * CANCELLED  → if previously COMPLETED, restores stock.
     */
    public void setStatus(int orderId, Order.Status newStatus) {
        Order order = getById(orderId);
        if (order == null) return;

        Order.Status old = order.getStatus();

        if (newStatus == Order.Status.COMPLETED && old == Order.Status.PENDING) {
            // deduct stock
            for (Order.Item item : order.getItems()) {
                Product p = productService.getById(item.getProductId());
                if (p != null) {
                    p.setStock(Math.max(0, p.getStock() - item.getQuantity()));
                    productService.update(p);
                }
            }
        } else if (newStatus == Order.Status.CANCELLED && old == Order.Status.COMPLETED) {
            // restore stock
            for (Order.Item item : order.getItems()) {
                Product p = productService.getById(item.getProductId());
                if (p != null) {
                    p.setStock(p.getStock() + item.getQuantity());
                    productService.update(p);
                }
            }
        }

        order.setStatus(newStatus);
        AppEventBus.publish();
    }

    // ── Queries ───────────────────────────────────────────────────────

    public List<Order> getAll()                          { return new ArrayList<>(orders); }
    public Order       getById(int id)                   {
        return orders.stream().filter(o -> o.getId() == id).findFirst().orElse(null);
    }
    public List<Order> getBySupplierId(int supplierId)   {
        return orders.stream()
                     .filter(o -> o.getSupplierId() == supplierId)
                     .collect(Collectors.toList());
    }
}