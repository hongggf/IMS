package com.inventory.service;

import com.inventory.core.AppEventBus;
import com.inventory.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductService {

    private final List<Product> products = new ArrayList<>();
    private final AtomicInteger idSeq = new AtomicInteger(1);

    public ProductService() {
        add(new Product(0, "Laptop", "Electronics", 999.00, 12));
        add(new Product(0, "Keyboard", "Accessories", 49.00, 3));
        add(new Product(0, "Monitor", "Electronics", 329.00, 8));
        add(new Product(0, "Mouse", "Accessories", 29.00, 0));
        add(new Product(0, "Desk Chair", "Furniture", 249.00, 5));
    }

    public List<Product> getAll() {
        return new ArrayList<>(products);
    }

    public Product getById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public void add(Product p) {
        p.setId(idSeq.getAndIncrement());
        products.add(p);
        AppEventBus.publish();
    }

    public void update(Product updated) {

    for (Product p : products) {
        if (p.getId() == updated.getId()) {

            p.setName(updated.getName());
            p.setCategory(updated.getCategory());
            p.setPrice(updated.getPrice());
            p.setStock(updated.getStock());

            AppEventBus.publish();
            return;
        }
    }
}


    public void delete(int id) {
        products.removeIf(p -> p.getId() == id);
        AppEventBus.publish();
    }

    public List<Product> search(String query) {
        String q = query.toLowerCase();
        List<Product> result = new ArrayList<>();

        for (Product p : products) {
            if (p.getName().toLowerCase().contains(q) ||
                p.getCategory().toLowerCase().contains(q)) {
                result.add(p);
            }
        }
        return result;
    }

    public long countLowStock() {
        return products.stream().filter(p -> p.getStock() <= 5).count();
    }

    public double totalValue() {
        return products.stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
    }
}