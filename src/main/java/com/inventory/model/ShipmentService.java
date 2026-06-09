package com.inventory.model;

import java.util.ArrayList;
import java.util.List;

public class ShipmentService {

    private final List<shipment> shipments = new ArrayList<>();

    public ShipmentService() {
        // Demo Data
        shipments.add(new shipment("SHP-001", "New York Warehouse", "In Transit", "2026-06-15"));
        shipments.add(new shipment("SHP-002", "Los Angeles Store", "Delivered", "2026-06-10"));
        shipments.add(new shipment("SHP-003", "Chicago Distribution Hub", "Pending", "2026-06-20"));
        shipments.add(new shipment("SHP-004", "Miami Retail", "In Transit", "2026-06-18"));
    }

    public List<shipment> getAll() {
        return new ArrayList<>(shipments); // Return copy to avoid modification issues
    }

    public void add(shipment s) {
        if (s != null) {
            shipments.add(s);
        }
    }

    public void delete(String id) {
        shipments.removeIf(s -> s.getId().equals(id));
    }

    // Optional: Get by ID
    public shipment getById(String id) {
        for (shipment s : shipments) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Optional: Update
    public void update(shipment updated) {
        for (int i = 0; i < shipments.size(); i++) {
            if (shipments.get(i).getId().equals(updated.getId())) {
                shipments.set(i, updated);
                return;
            }
        }
    }
}