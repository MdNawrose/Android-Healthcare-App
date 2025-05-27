package com.example.mediapp;

public class Product {
    private String name;
    private double price;
    private String image; // Example: "product1.jpg"
    private int quantity;
    private String description; // ✅ New field

    // Default constructor (required for Firestore)
    public Product() {}

    // Constructor with all fields
    public Product(String name, double price, String image, int quantity, String description) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
        this.description = description;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
