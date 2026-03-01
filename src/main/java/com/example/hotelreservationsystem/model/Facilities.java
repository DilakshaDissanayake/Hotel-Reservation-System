package com.example.hotelreservationsystem.model;

public class Facilities {
    private int id;
    private String name;
    private String category ;
    private String description;
    private String createdAt;

    public Facilities() {}

    public Facilities(int id, String name, String category, String description, String createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
