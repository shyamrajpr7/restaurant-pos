package com.restaurantpos.model;

public class MenuItem {
    private final String id;
    private final String name;
    private final double price;
    private final double taxPercent;
    private final String category;

    public MenuItem(String id, String name, double price, double taxPercent, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.taxPercent = taxPercent;
        this.category = category;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getTaxPercent() { return taxPercent; }
    public String getCategory() { return category; }

    @Override
    public String toString() { return name; }
}
