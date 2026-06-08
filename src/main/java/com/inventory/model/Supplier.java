package com.inventory.model;

public class Supplier {

    private int id;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String status;

    // ─────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────
    public Supplier(int id, String name, String contact, String phone, String email, String status) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }

    // ─────────────────────────────
    // GETTERS
    // ─────────────────────────────
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    // ─────────────────────────────
    // SETTERS (optional but useful later)
    // ─────────────────────────────
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}