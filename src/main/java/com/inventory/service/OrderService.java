package com.inventory.service;

import com.inventory.core.AppEventBus;
import com.inventory.model.Order;
import com.inventory.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final List<Order> orders = new ArrayList<>();
    private final ProductService productService;
    private int nextId = 1;

    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE ORDER
    // ─────────────────────────────────────────────────────────────

    public Order create(int supplierId, String supplierName) {

        Order order = new Order(
                nextId++,
                supplierId,
                supplierName
        );

        orders.add(order);

        AppEventBus.publish();

        return order;
    }

    // ─────────────────────────────────────────────────────────────
    // ADD ITEM
    // ─────────────────────────────────────────────────────────────

    public void addItem(
            int orderId,
            int productId,
            int quantity
    ) {

        Order order = getById(orderId);

        if (order == null)
            return;

        Product product =
                productService.getById(productId);

        if (product == null)
            return;

        order.addItem(
                new Order.Item(
                        productId,
                        product.getName(),
                        quantity,
                        product.getPrice()
                )
        );

        AppEventBus.publish();
    }

    // ─────────────────────────────────────────────────────────────
    // REMOVE ITEM
    // ─────────────────────────────────────────────────────────────

    public void removeItem(
            int orderId,
            int productId
    ) {

        Order order = getById(orderId);

        if (order == null)
            return;

        order.getItems().removeIf(
                item -> item.getProductId() == productId
        );

        AppEventBus.publish();
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE ORDER
    // ─────────────────────────────────────────────────────────────

    public void deleteOrder(int orderId) {

        Order order = getById(orderId);

        if (order == null)
            return;

        // Restore stock if completed order is deleted

        if (order.getStatus() == Order.Status.COMPLETED) {

            for (Order.Item item : order.getItems()) {

                Product product =
                        productService.getById(
                                item.getProductId()
                        );

                if (product != null) {

                    product.setStock(
                            product.getStock()
                                    + item.getQuantity()
                    );

                    productService.update(product);
                }
            }
        }

        orders.remove(order);

        AppEventBus.publish();
    }

    // ─────────────────────────────────────────────────────────────
    // STATUS CHANGE
    // ─────────────────────────────────────────────────────────────

    public void setStatus(
            int orderId,
            Order.Status newStatus
    ) {

        Order order = getById(orderId);

        if (order == null)
            return;

        Order.Status oldStatus =
                order.getStatus();

        if (newStatus == Order.Status.COMPLETED
                && oldStatus == Order.Status.PENDING) {

            for (Order.Item item : order.getItems()) {

                Product product =
                        productService.getById(
                                item.getProductId()
                        );

                if (product != null) {

                    product.setStock(
                            Math.max(
                                    0,
                                    product.getStock()
                                            - item.getQuantity()
                            )
                    );

                    productService.update(product);
                }
            }
        }

        else if (newStatus == Order.Status.CANCELLED
                && oldStatus == Order.Status.COMPLETED) {

            for (Order.Item item : order.getItems()) {

                Product product =
                        productService.getById(
                                item.getProductId()
                        );

                if (product != null) {

                    product.setStock(
                            product.getStock()
                                    + item.getQuantity()
                    );

                    productService.update(product);
                }
            }
        }

        order.setStatus(newStatus);

        AppEventBus.publish();
    }

    // ─────────────────────────────────────────────────────────────
    // QUERIES
    // ─────────────────────────────────────────────────────────────

    public List<Order> getAll() {
        return new ArrayList<>(orders);
    }

    public Order getById(int id) {

        return orders.stream()
                .filter(order -> order.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Order> getBySupplierId(
            int supplierId
    ) {

        return orders.stream()
                .filter(order ->
                        order.getSupplierId()
                                == supplierId)
                .collect(Collectors.toList());
    }
    // Add this to your OrderService class
public List<Order> getActiveOrders() {
    return orders.stream()
            .filter(o -> o.getStatus() != Order.Status.COMPLETED 
                      && o.getStatus() != Order.Status.CANCELLED)
            .collect(Collectors.toList());
}
}