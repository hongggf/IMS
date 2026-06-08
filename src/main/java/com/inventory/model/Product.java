package com.inventory.model;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private int stock;

    public Product() {}

    public Product(int id, String name, String category, double price, int stock) {
        this.id       = id;
        this.name     = name;
        this.category = category;
        this.price    = price;
        this.stock    = stock;
    }

    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getCategory() { return category; }
    public double getPrice()    { return price; }
    public int    getStock()    { return stock; }

    public void setId(int id)             { this.id       = id; }
    public void setName(String name)      { this.name     = name; }
    public void setCategory(String cat)   { this.category = cat; }
    public void setPrice(double price)    { this.price    = price; }
    public void setStock(int stock)       { this.stock    = stock; }

    public String getStatus() {
        if (stock == 0)  return "Out";
        if (stock <= 5)  return "Low";
        return "OK";
    }
}
