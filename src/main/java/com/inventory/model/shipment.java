package com.inventory.model;

public class shipment {
    private String id;
    private String destination;
    private String status;
    private String eta;

    public shipment(String id, String destination, String status, String eta) {
        this.id = id;
        this.destination = destination;
        this.status = status;
        this.eta = eta;
    }

    // Getters
    public String getId() { return id; }
    public String getDestination() { return destination; }
    public String getStatus() { return status; }
    public String getEta() { return eta; }
}