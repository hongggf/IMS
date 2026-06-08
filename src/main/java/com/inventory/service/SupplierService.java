package com.inventory.service;

import com.inventory.core.AppEventBus;
import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;
import java.util.ArrayList;
import java.util.List;

public class SupplierService {

    private final List<Supplier> suppliers = new ArrayList<>();
    private int nextId = 1;

    public SupplierService() {

        // demo data
        suppliers.add(new Supplier(nextId++, "ABC Trading", "Dara", "012345678", "abc@mail.com", "Active"));
        suppliers.add(new Supplier(nextId++, "Khmer Supply", "Sok", "098765432", "khmer@mail.com", "Inactive"));
    }

    // ─────────────────────────────
    // GET ALL
    // ─────────────────────────────
    public List<Supplier> getAll() {
        return suppliers;
    }

    // ─────────────────────────────
    // ADD
    // ─────────────────────────────
    public void add(Supplier supplier) {
        supplier.setId(nextId++);
        suppliers.add(supplier);

        
    }

    // ─────────────────────────────
    // DELETE (optional)
    // ─────────────────────────────
    public void deleteById(int id) {
        suppliers.removeIf(s -> s.getId() == id);
    }

    public void update(Supplier supplier) {
    // 1. If you are using a List/Memory:
    for (int i = 0; i < suppliers.size(); i++) {
        if (suppliers.get(i).getId() == supplier.getId()) {
            suppliers.set(i, supplier);
            break;
        }
    }
}

  public Supplier getById(int id) {
    return suppliers.stream()
            .filter(s -> s.getId() == id)
            .findFirst()
            .orElse(null);
}

public void delete(int id) {
    suppliers.removeIf(s -> s.getId() == id);
    AppEventBus.publish();
}}