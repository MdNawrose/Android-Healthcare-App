package com.example.mediapp;

import java.io.Serializable;

public class Doctor implements Serializable {
    private String id; // 🔥 Unique identifier for Firestore
    private String name;
    private String specialty;
    private double rating;
    private String distance;
    private String image;
    private String description;

    public Doctor() {} // Required for Firestore

    public Doctor(String id, String name, String specialty, double rating, String distance, String image, String description) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.rating = rating;
        this.distance = distance;
        this.image = image;
        this.description = description;
    }

    public String getId() { return id; } // 🔥 Getter for ID
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }
    public double getRating() { return rating; }
    public String getDistance() { return distance; }
    public String getImage() { return image; }
    public String getDescription() { return description; }

    public void setId(String id) { this.id = id; } // 🔥 Setter for ID
}
