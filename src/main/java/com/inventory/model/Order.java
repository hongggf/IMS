package com.inventory.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    public enum Status { PENDING, COMPLETED, CANCELLED }

    public static class Item {
        private int productId;
        private String productName;
        private int quantity;
        private double unitPrice;

        public Item(int productId, String productName, int quantity, double unitPrice) {
            this.productId   = productId;
            this.productName = productName;
            this.quantity    = quantity;
            this.unitPrice   = unitPrice;
        }

        public int    getProductId()   { return productId; }
        public String getProductName() { return productName; }
        public int    getQuantity()    { return quantity; }
        public double getUnitPrice()   { return unitPrice; }
        public double getSubtotal()    { return quantity * unitPrice; }
    }

    private int         id;
    private int         supplierId;
    private String      supplierName;
    private Status      status;
    private List<Item>  items;

    public Order(int id, int supplierId, String supplierName) {
        this.id           = id;
        this.supplierId   = supplierId;
        this.supplierName = supplierName;
        this.status       = Status.PENDING;
        this.items        = new ArrayList<>();
    }

    public void addItem(Item item) { items.add(item); }

    public double getTotal() {
        return items.stream().mapToDouble(Item::getSubtotal).sum();
    }

    // ── getters / setters ─────────────────────────────────────────────
    public int         getId()           { return id; }
    public int         getSupplierId()   { return supplierId; }
    public String      getSupplierName() { return supplierName; }
    public Status      getStatus()       { return status; }
    public List<Item>  getItems()        { return items; }

    public void setId(int id)                   { this.id = id; }
    public void setStatus(Status status)        { this.status = status; }
    public void setSupplierName(String name)    { this.supplierName = name; }
}